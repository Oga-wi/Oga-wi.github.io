function classement(ids) {
    console.log("Appel de classement() avec l'ids :", ids);

    const tbody = document.querySelector("#classement tbody");
    tbody.innerHTML = "";
    fetch("http://localhost:8080/SAE23_Web_API/Skipper_JSON?ids=" + ids)
        .then(r => r.json())
        .then(mafonction)
        .catch(err => console.error("Erreur fetch :", err));

    function mafonction(d) {
        console.log("JSON reçu :", d);
        document.getElementById("name").textContent = d.skipper;
        document.getElementById("date").textContent = new Date(d.naissance * 1000).toLocaleDateString('fr-FR');
        document.getElementById("lieu").textContent = d.nationalite;


        d.palmares.forEach(participant => {
            const ligne = document.createElement("tr");

            if (participant.place === "DNF") {
                ligne.classList.add("table-danger");
            }
            if (String(participant.place) === "1") {
                ligne.classList.add("table-success");
            }

            ligne.innerHTML = `
                <th scope="row">${participant.place}</th>
                <td><a href="bateau.html?idb=${participant.idb}">${participant.bateau}</a></td>
                <td><a href="course.html?idc=${participant.idc}">${participant.edition}<a></td>
            `;

            tbody.appendChild(ligne);
        });
    }
}

window.addEventListener("DOMContentLoaded", () => {
    const selectSkipper = document.getElementById("select-Skipper");
    const params = new URLSearchParams(window.location.search);
    let idsValue = params.get('ids');

    if (idsValue) {
        classement(idsValue);
        if (selectSkipper) selectSkipper.value = idsValue;
    } else {
        console.log("Aucun paramètre URL");
        classement("0");
        if (selectSkipper) selectSkipper.value = "0";
    }

    if (selectSkipper) {
        selectSkipper.addEventListener("change", (event) => {
            const selectedId = event.target.value;
            if (selectedId) {
                console.log("Nouveau Skipper sélectionné :", selectedId);
                classement(selectedId);

                const newUrl = window.location.protocol + "//" + window.location.host
                    + window.location.pathname + '?ids=' + selectedId;
                window.history.pushState({ path: newUrl }, '', newUrl);
            }
        });
    }
});