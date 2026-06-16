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

public class Course extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='fr'>");
            out.println("<head><meta charset='UTF-8'><title>Classement</title></head>");
            out.println("<body>");

            String idcParam = request.getParameter("idc");

            if (idcParam == null || idcParam.isEmpty()) {
                out.println("<p style='color:red;'>Paramètre 'idc' manquant. Exemple : /Course?idc=42</p>");
            } else {
                try {
                    int idc = Integer.parseInt(idcParam);

                    InitialContext cxt = new InitialContext();
                    DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/postgres");

                    try (Connection conn = ds.getConnection()) {

                        PreparedStatement stmt = conn.prepareStatement("""
                            SELECT classement.place, classement.nombateau, skipper.nom, skipper.prenom, course.edition, classement.idb
                            FROM classement
                            INNER JOIN skipper ON skipper.ids = classement.ids
                            INNER JOIN course ON course.idc = classement.idc
                            WHERE classement.idc = ?
                            ORDER BY classement.place;
                        """);

                        stmt.setInt(1, idc);
                        ResultSet result = stmt.executeQuery();

                        if (result.next()) {
                            String edition = result.getString("edition");
                            out.println("<h1>Classement de l'édition " + edition + "</h1>");

                            out.println("<table border='1'>");
                            out.println("<thead>");
                            out.println("<tr><th>Place</th><th>Bateau</th><th>Skipper</th></tr>");
                            out.println("</thead>");
                            out.println("<tbody>");

                            do {
                                String place = result.getObject("place") != null ? result.getString("place") : "DNF";
                                out.println("<tr>");
                                out.println("<td>" + place + "</td>");
                                out.println("<td>" + result.getString("nombateau") + "</td>");
                                out.println("<td>" + result.getString("prenom") + " " + result.getString("nom") + "</td>");
                                out.println("</tr>");
                            } while (result.next());

                            out.println("</tbody>");
                            out.println("</table>");

                        } else {
                            out.println("<p style='color:red;'>Aucun résultat pour cette édition.</p>");
                        }

                    } catch (SQLException e) {
                        out.println("<p style='color:red;'>Erreur SQL : " + e.getMessage() + "</p>");
                    }

                } catch (NumberFormatException e) {
                    out.println("<p style='color:red;'>Paramètre 'idc' invalide.</p>");
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
        return "Servlet affichant le classement d'une édition en HTML";
    }
}