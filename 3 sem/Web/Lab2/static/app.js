const SERVER_URL = "/fcgi-bin/server.jar";

const canvas = document.getElementById("plane");
const ctx = canvas.getContext("2d");
let lastPoints = [];  


const rBox = document.getElementById("r-checkboxes");
for (let r = 1; r <= 5; r++) {
    rBox.innerHTML += `<label><input type="checkbox" name="r" value="${r}"> ${r}</label> `;
}


const fracLen = s => (s.split(".")[1] || "").length;
const toBig = (s, k) => {
    const [i, f = ""] = s.replace("-", "").split(".");
    return (s[0] === "-" ? -1n : 1n) * BigInt(i + f.padEnd(k, "0"));
};


function checkNumber(value, min, max) {
    if (!/^-?\d+(\.\d+)?$/.test(value)) return "Введите число!";
    const k = fracLen(value), v = toBig(value, k);
    if (v <= toBig(min, k) || v >= toBig(max, k)) return `Число должно быть в интервале (${min} ... ${max}).`;
    return "";
}


function drawPlane() {
    const size = canvas.width, c = size / 2;
    const R = 140;
    ctx.clearRect(0, 0, size, size);

    // область
    ctx.fillStyle = "rgba(33,150,243,0.7)";
    ctx.beginPath();
    ctx.rect(c, c - R, R / 2, R);                              // прямоуг
    ctx.moveTo(c, c);
    ctx.arc(c, c, R / 2, Math.PI, 1.5 * Math.PI);              // сектор
    ctx.moveTo(c, c);
    ctx.lineTo(c + R, c);
    ctx.lineTo(c, c + R);                                      // треуг
    ctx.fill();

    // оси
    ctx.strokeStyle = ctx.fillStyle = "black";
    ctx.font = "12px Arial";
    ctx.beginPath();
    ctx.moveTo(20, c); ctx.lineTo(size - 20, c);
    ctx.moveTo(c, size - 20); ctx.lineTo(c, 20);
    ctx.stroke();
    ctx.fillText("x", size - 20, c - 8);
    ctx.fillText("y", c + 8, 25);
    const marks = [[-1, "-R"], [-0.5, "-R/2"], [0.5, "R/2"], [1, "R"]];
    for (const [k, text] of marks) {
        ctx.fillText(text, c + k * R - 10, c + 16);    // на оси X
        ctx.fillText(text, c + 6, c - k * R + 4);      // на оси Y
    }


    for (const p of lastPoints) {
        ctx.fillStyle = p.hit ? "green" : "red";
        ctx.beginPath();
        ctx.arc(c + p.x / p.r * R, c - p.y / p.r * R, 5, 0, 2 * Math.PI);
        ctx.fill();
    }
}


function showTable(history) {
    let html = "";
    for (const row of history) {
        html += `<tr>
            <td>${row.x}</td><td>${row.y}</td><td>${row.r}</td>
            <td class="${row.hit ? "result-hit" : "result-miss"}">${row.hit ? "Попадание" : "Промах"}</td>
            <td>${row.time}</td><td>${row.micros}</td>
        </tr>`;
    }
    document.getElementById("results-body").innerHTML = html;
    document.getElementById("empty-msg").style.display = history.length ? "none" : "block";
}


function sendRequest(method, body, pointsCount) {
    const serverError = document.getElementById("server-error");
    serverError.textContent = "";

    fetch(SERVER_URL, {
        method: method,
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: body
    })
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                serverError.textContent = "Ошибка сервера: " + data.error;
                return;
            }
            lastPoints = pointsCount ? data.history.slice(-pointsCount) : [];
            showTable(data.history);
            drawPlane();
            document.getElementById("server-info").textContent =
                `Время сервера: ${data.currentTime}. Время работы скрипта: ${data.micros} мкс`;
        })
        .catch(() => serverError.textContent = "Сервер недоступен");
}


document.getElementById("point-form").addEventListener("submit", event => {
    event.preventDefault(); 
    const x = document.getElementById("x-input").value.trim().replace(",", ".");
    const y = document.getElementById("y-input").value.trim().replace(",", ".");
    const rs = [...document.querySelectorAll("#r-checkboxes input:checked")].map(cb => cb.value);

    const xError = checkNumber(x, "-5", "5");
    const yError = checkNumber(y, "-3", "5");
    const rError = rs.length ? "" : "Выберите хотя бы одно значение R.";
    document.getElementById("x-error").textContent = xError;
    document.getElementById("y-error").textContent = yError;
    document.getElementById("r-error").textContent = rError;
    if (xError || yError || rError) return;

    let body = `x=${encodeURIComponent(x)}&y=${encodeURIComponent(y)}`;
    for (const r of rs) body += `&r=${r}`;
    sendRequest("POST", body, rs.length);
});

document.getElementById("clear-btn").addEventListener("click", () => {
    sendRequest("POST", "action=clear", 0);
});

drawPlane();
sendRequest("GET", null, 0); 
