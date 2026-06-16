public class ClassementWrp {
    private int place;
    private String prenom;
    private String nom;
    private String nombateau;

    public ClassementWrp(int place, String prenom, String nom, String nombateau) {
        this.place = place;
        this.prenom = prenom;
        this.nom = nom;
        this.nombateau = nombateau;
    }

    public int getPlace() {
        return place;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getNombateau() {
        return nombateau;
    }
}