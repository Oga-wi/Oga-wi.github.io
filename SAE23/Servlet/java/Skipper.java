import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Skipper extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='fr'>");
            out.println("<head><meta charset='UTF-8'><title>Palmarès</title></head>");
            out.println("<body>");

            String idsParam = request.getParameter("ids");

            if (idsParam == null || idsParam.isEmpty()) {
                out.println("<p style='color:red;'>Paramètre 'ids' manquant. Exemple : /Skipper?ids=2</p>");
            } else {
                try {
                    int ids = Integer.parseInt(idsParam);

                    InitialContext cxt = new InitialContext();
                    DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/postgres");

                    try (Connection conn = ds.getConnection()) {

                        PreparedStatement stmt = conn.prepareStatement("""
                            SELECT classement.place, classement.nombateau, skipper.nom, skipper.prenom, course.edition
                            FROM classement
                            INNER JOIN skipper ON skipper.ids = classement.ids
                            INNER JOIN course ON course.idc = classement.idc
                            WHERE classement.ids = ?
                            ORDER BY classement.place;
                        """);

                        stmt.setInt(1, ids);
                        ResultSet result = stmt.executeQuery();

                        if (result.next()) {
                            String nom = result.getString("nom");
                            String prenom = result.getString("prenom");
                            out.println("<h1>Palmarès de " + prenom + " " + nom + "</h1>");

                            out.println("<table border='1'>");
                            out.println("<thead>");
                            out.println("<tr><th>Place</th><th>Bateau</th><th>Édition</th></tr>");
                            out.println("</thead>");
                            out.println("<tbody>");

                            do {
                                String place = result.getObject("place") != null ? result.getString("place") : "DNF";
                                out.println("<tr>");
                                out.println("<td>" + place + "</td>");
                                out.println("<td>" + result.getString("nombateau") + "</td>");
                                out.println("<td>" + result.getString("edition") + "</td>");
                                out.println("</tr>");
                            } while (result.next());

                            out.println("</tbody>");
                            out.println("</table>");

                        } else {
                            out.println("<p style='color:red;'>Aucun résultat pour ce skipper.</p>");
                        }

                    } catch (SQLException e) {
                        out.println("<p style='color:red;'>Erreur SQL : " + e.getMessage() + "</p>");
                    }

                } catch (NumberFormatException e) {
                    out.println("<p style='color:red;'>Paramètre 'ids' invalide.</p>");
                } catch (NamingException e) {
                    out.println("<p style='color:red;'>Erreur de configuration JNDI.</p>");
                }
            }

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet affichant le palmarès d'un skipper en HTML";
    }
}