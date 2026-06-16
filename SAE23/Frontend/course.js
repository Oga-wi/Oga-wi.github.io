function classement(idc) {
    console.log("idc :", idc);

    fetch("http://localhost:8080/SAE23_Web_API/Course_JSON?idc=" + idc)
        .then(r => r.json())
        .then(mafonction)

    function mafonction(d) {
        console.log(d);

        if (d.success) {
            const titreEdition = document.getElementById("edition");
            titreEdition.textContent = "Vendée Globe " + d.edition;

            const tbody = document.querySelector("#classement tbody");
            tbody.innerHTML = "";

            d.classement.forEach(participant => {
                const ligne = document.createElement("tr");

                // Ajoute la classe au CSS
                if (participant.place === "DNF") {
                    ligne.classList.add("table-danger");
                }

                if (participant.place === "1") {
                    ligne.classList.add("table-success");
                }

                ligne.innerHTML = `
                    <th scope="row">${participant.place}</th>
                    <td><a href="skipper.html?ids=${participant.ids}">${participant.skipper}</a></td>
                    <td><a href="bateau.html?idb=${participant.idb}">${participant.bateau}</a></td>
                `;

                tbody.appendChild(ligne);
            });

        } else {
            console.error("Erreur du serveur :", d.error);
        }
    }
}

window.addEventListener("DOMContentLoaded", () => {

    const selectcourse = document.getElementById("select-course");
    const params = new URLSearchParams(window.location.search);
    let idcValue = params.get('idc');

    if (idcValue) {
        classement(idcValue);
        if (selectcourse) selectcourse.value = idcValue;
    } else {
        console.log("Aucun paramètre URL");
        classement("3");
        if (selectcourse) selectcourse.value = "3";
    }

    if (selectcourse) {
        selectcourse.addEventListener("change", (event) => {
            const selectedId = selectcourse.value;
            if (selectedId) {
                console.log("Nouveau course sélectionné :", selectedId);
                classement(selectedId);

                const newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?idc=' + selectedId;
                window.history.pushState({ path: newUrl }, '', newUrl);
            }
        });
    }
});