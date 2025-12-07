function showInfo(text) {
	document.getElementById("infoText").innerText = text;
	document.getElementById("infoModal").style.display = "flex";
}

function closeModal() {
	document.getElementById("infoModal").style.display = "none";
}

window.onclick = function(event) {
	let modal = document.getElementById("infoModal");
	if (event.target === modal) {
		closeModal();
	}
}

document.addEventListener("DOMContentLoaded", () => {
    const dropdown = document.getElementById("regionSelect");
    const button = document.getElementById("searchBtn");

    // restore selected option
    const saved = localStorage.getItem("selectedRegion");
    if (saved) dropdown.value = saved;

    dropdown.addEventListener("change", () => {
        localStorage.setItem("selectedRegion", dropdown.value);
    });

    button.addEventListener("click", () => {
        const region = encodeURIComponent(dropdown.value);
        window.location.href = `/search?region=${region}`;
    });
});