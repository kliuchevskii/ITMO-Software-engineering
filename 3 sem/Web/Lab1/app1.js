"use strict";

const X_VALUES = ["-2", "-1.5", "-1", "-0.5", "0", "0.5", "1", "1.5", "2"];
const R_VALUES = ["1", "2", "3", "4", "5"];
const Y_LIMIT = "3"; // Y ∈ [-3; 3]
const KEY = "labVariantResults";

const $ = id => document.getElementById(id);
const canvas = $("plane"), ctx = canvas.getContext("2d");
let lastPoint = null;


const fracLen = s => (s.split(".")[1] || "").length;
const toBig = (s, k) => {
    const [i, f = ""] = s.replace("-", "").split(".");
    return (s[0] === "-" ? -1n : 1n) * BigInt(i + f.padEnd(k, "0"));
};

function isHit(xs, ys, rs) {
    const k = Math.max(fracLen(xs), fracLen(ys), fracLen(rs));
    const [x, y, r] = [xs, ys, rs].map(s => toBig(s, k));
    return (x >= -r && x <= 0n && y >= 0n && 2n * y <= r)        // прямоуг
        || (x <= 0n && y <= 0n && 4n * (x * x + y * y) <= r * r)  // сектор
        || (x >= 0n && y >= 0n && 2n * x + y <= r);               // треуг
}

function validateY(s) {
    if (!/^-?\d+(\.\d+)?$/.test(s)) return "Введите число!";
    const y = toBig(s, fracLen(s)), lim = toBig(Y_LIMIT, fracLen(s));
    return y < -lim || y > lim ? `Значение должно быть в диапазоне от -${Y_LIMIT} до ${Y_LIMIT}.` : "";
}




const selected = id => document.querySelector(`#${id} input:checked`)?.value ?? null;

function buildChoices(id, values, type) {
    $(id).innerHTML = values.map((v, i) =>
        `<span><input type="${type}" name="${id}" value="${v}" id="${id}-${i}"${type === "radio" && i === 0 ? " checked" : ""}>` +
        `<label for="${id}-${i}">${v}</label></span>`).join("");
}


function drawPlane() {
    const R = +selected("r-radius");
    const W = canvas.width, c = W / 2, s = (c - 40) / (R + 1); // центр
    const e = (R + 1) * s;                                      // полуось
    ctx.clearRect(0, 0, W, W);


    ctx.fillStyle = "rgba(33,150,243,0.7)";
    ctx.beginPath();
    ctx.rect(c - R * s, c - R / 2 * s, R * s, R / 2 * s);                    // прямоуг
    ctx.moveTo(c, c); ctx.arc(c, c, R / 2 * s, Math.PI / 2, Math.PI);        // сектор
    ctx.moveTo(c, c); ctx.lineTo(c + R / 2 * s, c); ctx.lineTo(c, c - R * s); // треуг
    ctx.fill();

    // Оси
    ctx.fillStyle = ctx.strokeStyle = "#333";
    ctx.font = "12px Arial";
    ctx.beginPath();
    ctx.moveTo(c - e, c); ctx.lineTo(c + e, c);
    ctx.moveTo(c, c + e); ctx.lineTo(c, c - e);
    [[-R, "-R"], [-R / 2, "-R/2"], [R / 2, "R/2"], [R, "R"]].forEach(([m, t]) => {
        ctx.moveTo(c + m * s, c - 4); ctx.lineTo(c + m * s, c + 4);
        ctx.moveTo(c - 4, c - m * s); ctx.lineTo(c + 4, c - m * s);
        ctx.fillText(t, c + m * s - 10, c + 16);
        ctx.fillText(t, c + 6, c - m * s + 4);
    });
    ctx.stroke();

    // Стрелки и подписи осей
    ctx.beginPath();
    ctx.moveTo(c + e, c); ctx.lineTo(c + e - 8, c - 4); ctx.lineTo(c + e - 8, c + 4);
    ctx.moveTo(c, c - e); ctx.lineTo(c - 4, c - e + 8); ctx.lineTo(c + 4, c - e + 8);
    ctx.fill();
    ctx.fillText("x", c + e - 10, c - 8);
    ctx.fillText("y", c + 6, c - e + 10);


    if (lastPoint && +lastPoint.r === R) {
        ctx.beginPath();
        ctx.arc(c + lastPoint.x * s, c - lastPoint.y * s, 6, 0, 2 * Math.PI);
        ctx.fillStyle = lastPoint.hit ? "#2e7d32" : "#c62828";
        ctx.strokeStyle = "#000";
        ctx.fill(); ctx.stroke();
    }
}

// История
const load = () => { try { return JSON.parse(localStorage.getItem(KEY)) || []; } catch { return []; } };

function renderTable() {
    const rows = load();
    $("results-body").innerHTML = rows.map(e =>
        `<tr><td>${e.x}</td><td>${e.y}</td><td>${e.r}</td>` +
        `<td class="${e.hit ? "result-hit" : "result-miss"}">Точка ${e.hit ? "" : "не "}попадает в заданную область</td>` +
        `<td>${new Date(e.timestamp).toLocaleString("ru-RU")}</td></tr>`).join("");
    $("empty-msg").style.display = rows.length ? "none" : "block";
}


$("point-form").addEventListener("submit", evt => {
    evt.preventDefault();
    const x = selected("x-checkboxes"), r = selected("r-radius");
    const y = $("y-input").value.trim().replace(",", ".");
    const xErr = x === null ? "Выберите значение X." : "", yErr = validateY(y);
    $("x-error").textContent = xErr;
    $("y-error").textContent = yErr;
    $("y-input").classList.toggle("invalid", !!yErr);
    if (xErr || yErr) return;

    lastPoint = { x, y, r, hit: isHit(x, y, r), timestamp: new Date().toISOString() };
    localStorage.setItem(KEY, JSON.stringify([...load(), lastPoint]));
    renderTable();
    drawPlane();
});

$("x-checkboxes").addEventListener("change", evt => { // X — только одно значение
    $("x-checkboxes").querySelectorAll("input").forEach(cb => { if (cb !== evt.target) cb.checked = false; });
    $("x-error").textContent = "";
    drawPlane();
});
$("r-radius").addEventListener("change", drawPlane);
$("y-input").addEventListener("input", () => { $("y-input").classList.remove("invalid"); $("y-error").textContent = ""; });
$("clear-btn").addEventListener("click", () => { localStorage.removeItem(KEY); renderTable(); });

buildChoices("x-checkboxes", X_VALUES, "checkbox");
buildChoices("r-radius", R_VALUES, "radio");
renderTable();
drawPlane();
