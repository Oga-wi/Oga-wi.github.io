import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.naming.*;
import javax.sql.DataSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/Bateau")
public class Bateau_JSON extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idbParam = request.getParameter("idb");
        JsonObject root = new JsonObject();

        if (idbParam == null || idbParam.isEmpty()) {
            root.addProperty("success", false);
            root.addProperty("error", "Parametre 'idb' manquant. Exemple : /Bateau?idb=10");
            out.print(root.toString());
            return;
        }

        try {
            int idb = Integer.parseInt(idbParam);
            DataSource ds = (DataSource) new InitialContext().lookup("java:/comp/env/jdbc/postgres");

            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("""
                     SELECT classement.place, classement.nombateau,
                            skipper.nom, skipper.prenom,
                            course.edition, skipper.ids, bateau.architecte, course.idc, bateau.misealeau
                     FROM classement
                     INNER JOIN skipper ON skipper.ids = classement.ids
                     INNER JOIN course  ON course.idc  = classement.idc
                     INNER JOIN bateau  ON bateau.idb  = classement.idb
                     WHERE classement.idb = ?
                     ORDER BY classement.place
                 """)) {

                stmt.setInt(1, idb);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        root.addProperty("success", false);
                        root.addProperty("error", "Aucun resultat pour ce bateau.");
                        out.print(root.toString());
                        return;
                    }

                    root.addProperty("success", true);
                    root.addProperty("bateau", rs.getString("nombateau"));
                    root.addProperty("architecte", rs.getString("architecte"));
                    root.addProperty("misealeau", rs.getString("misealeau"));


                    JsonArray palmares = new JsonArray();
                    do {
                        JsonObject entry = new JsonObject();

                        int place = rs.getInt("place");
                        if (rs.wasNull()) {
                            entry.addProperty("place", "DNF");
                        } else {
                            entry.addProperty("place", place);
                        }

                        entry.addProperty("skipper", rs.getString("prenom") + " " + rs.getString("nom"));
                        entry.addProperty("edition", rs.getString("edition"));
                        entry.addProperty("ids", rs.getInt("ids"));
                        entry.addProperty("idc", rs.getInt("idc"));

                        String architecte = rs.getString("architecte");
                        entry.addProperty("architecte", architecte != null ? architecte : "");

                        palmares.add(entry);
                    } while (rs.next());

                    root.add("palmares", palmares);
                    out.print(root.toString());
                }
            }
        } catch (NumberFormatException e) {
            root.addProperty("success", false);
            root.addProperty("error", "Parametre 'idb' invalide.");
            out.print(root.toString());
        } catch (SQLException e) {
            root.addProperty("success", false);
            root.addProperty("error", e.getMessage());
            out.print(root.toString());
        } catch (NamingException e) {
            root.addProperty("success", false);
            root.addProperty("error", "Erreur de configuration JNDI.");
            out.print(root.toString());
        }
    }
}