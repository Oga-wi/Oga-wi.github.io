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

@WebServlet("/Course")
public class Course_JSON extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idcParam = request.getParameter("idc");
        JsonObject root = new JsonObject();

        if (idcParam == null || idcParam.isEmpty()) {
            root.addProperty("success", false);
            root.addProperty("error", "Parametre 'idc' manquant. Exemple : /Course?idc=42");
            out.print(root.toString());
            return;
        }

        try {
            int idc = Integer.parseInt(idcParam);
            DataSource ds = (DataSource) new InitialContext().lookup("java:/comp/env/jdbc/postgres");

            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("""
                     SELECT classement.place, classement.nombateau,
                            skipper.nom, skipper.prenom, skipper.ids, classement.idb, 
                            course.edition
                     FROM classement
                     INNER JOIN skipper ON skipper.ids = classement.ids
                     INNER JOIN course  ON course.idc  = classement.idc
                     WHERE classement.idc = ?
                     ORDER BY classement.place
                 """)) {

                stmt.setInt(1, idc);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        root.addProperty("success", false);
                        root.addProperty("error", "Aucun resultat pour cette edition.");
                        out.print(root.toString());
                        return;
                    }

                    root.addProperty("success", true);
                    root.addProperty("edition", rs.getString("edition"));

                    JsonArray classement = new JsonArray();
                    do {
                        JsonObject entry = new JsonObject();

                        int place = rs.getInt("place");
                        if (rs.wasNull()) {
                            entry.addProperty("place", "DNF");
                        } else {
                            entry.addProperty("place", place);
                        }
                        
                        entry.addProperty("ids", rs.getString("ids"));
                        entry.addProperty("idb", rs.getString("idb"));
                        entry.addProperty("bateau", rs.getString("nombateau"));
                        entry.addProperty("skipper", rs.getString("prenom") + " " + rs.getString("nom"));

                        classement.add(entry);
                    } while (rs.next());

                    root.add("classement", classement);
                    out.print(root.toString());
                }
            }
        } catch (NumberFormatException e) {
            root.addProperty("success", false);
            root.addProperty("error", "Parametre 'idc' invalide.");
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