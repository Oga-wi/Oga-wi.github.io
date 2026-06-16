function classement(idb) {
    console.log("Appel de classement() avec l'idb :", idb);

    fetch("http://localhost:8080/SAE23_Web_API/Bateau_JSON?idb=" + idb)
        .then(r => r.json())
        .then(mafonction)

    function mafonction(d) {
        console.log("JSON reçu au lancement :", d);

        if (d && d.bateau && d.palmares) {
            const titre = document.getElementById("bateau");
            titre.textContent = "Palmarès de : " + d.bateau;

            const architecte = document.getElementById("architecte");
            architecte.textContent = "Architecte : " + d.architecte;

            const misealeau = document.getElementById("date");
            misealeau.textContent = "Mise à l'eau : " + d.misealeau;

            const tbody = document.querySelector("#classement tbody");
            tbody.innerHTML = "";

            d.palmares.forEach(participant => {
                const ligne = document.createElement("tr");

                if (participant.place === "DNF") {
                    ligne.classList.add("table-danger");
                }
                if (participant.place === "1") {
                    ligne.classList.add("table-success");
                }

                ligne.innerHTML = `
                    <th scope="row">${participant.place}</th>
                    <td><a href="skipper.html?ids=${participant.ids}">${participant.skipper}</a></td>
                    <td></td>
                    <td><a href="course.html?idc=${participant.idc}">${participant.edition}</a></td>
                `;

                tbody.appendChild(ligne);
            });

        } else {
            console.error("Erreur : Le format du JSON reçu est incorrect.", d);
        }
    }
}

window.addEventListener("DOMContentLoaded", () => {

    const selectBateau = document.getElementById("select-bateau");
    const params = new URLSearchParams(window.location.search);
    let idbValue = params.get('idb');

    if (idbValue) {
        classement(idbValue);
        if (selectBateau) selectBateau.value = idbValue;
    } else {
        console.log("Aucun paramètre URL");
        classement("0");
        if (selectBateau) selectBateau.value = "0";
    }

    if (selectBateau) {
        selectBateau.addEventListener("change", (event) => {
            const selectedId = event.target.value;
            if (selectedId) {
                console.log("Nouveau bateau sélectionné :", selectedId);
                classement(selectedId);

                const newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?idb=' + selectedId;
                window.history.pushState({ path: newUrl }, '', newUrl);
            }
        });
    }
});