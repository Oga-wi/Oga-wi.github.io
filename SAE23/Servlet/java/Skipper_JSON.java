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

@WebServlet("/Skipper")
public class Skipper_JSON extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idsParam = request.getParameter("ids");
        JsonObject root = new JsonObject();

        if (idsParam == null || idsParam.isEmpty()) {
            root.addProperty("success", false);
            root.addProperty("error", "Parametre 'ids' manquant. Exemple : /Skipper?ids=2");
            out.print(root.toString());
            return;
        }

        try {
            int ids = Integer.parseInt(idsParam);
            DataSource ds = (DataSource) new InitialContext().lookup("java:/comp/env/jdbc/postgres");

            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("""
                     SELECT classement.place, classement.nombateau,
                            skipper.nom, skipper.prenom, skipper.ids, skipper.naissance, skipper.nationalite,
                            course.edition, course.idc, classement.idb
                     FROM classement
                     INNER JOIN skipper ON skipper.ids = classement.ids
                     INNER JOIN course  ON course.idc  = classement.idc
                     WHERE classement.ids = ?
                     ORDER BY classement.place
                 """)) {

                stmt.setInt(1, ids);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        root.addProperty("success", false);
                        root.addProperty("error", "Aucun resultat pour ce skipper.");
                        out.print(root.toString());
                        return;
                    }

                    root.addProperty("success", true);
                    root.addProperty("skipper", rs.getString("prenom") + " " + rs.getString("nom"));
                    root.addProperty("nationalite", rs.getString("nationalite"));
                    root.addProperty("naissance", rs.getString("naissance"));
                    
                    root.addProperty("ids", rs.getString("ids"));
                    
                    JsonArray palmares = new JsonArray();
                    do {
                        JsonObject entry = new JsonObject();

                        int place = rs.getInt("place");
                        if (rs.wasNull()) {
                            entry.addProperty("place", "DNF");
                        } else {
                            entry.addProperty("place", place);
                        }
                        
                        entry.addProperty("idb", rs.getString("idb"));
                        entry.addProperty("idc", rs.getString("idc"));
                        entry.addProperty("bateau", rs.getString("nombateau"));
                        entry.addProperty("edition", rs.getString("edition"));

                        palmares.add(entry);
                    } while (rs.next());

                    root.add("palmares", palmares);
                    out.print(root.toString());
                }
            }
        } catch (NumberFormatException e) {
            root.addProperty("success", false);
            root.addProperty("error", "Parametre 'ids' invalide.");
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