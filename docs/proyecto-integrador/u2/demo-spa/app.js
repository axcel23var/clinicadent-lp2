const KEY = "comarket_erp_u2_spa";
let token = sessionStorage.getItem("token") || "";
const $ = (selector) => document.querySelector(selector);
const data = () => JSON.parse(localStorage.getItem(KEY) || "[]");
const save = (ventas) => localStorage.setItem(KEY, JSON.stringify(ventas));
const api = (action) => token ? action() : { ok: false, error: "401 Token requerido" };

function guard() {
  $("#loginPanel").classList.toggle("hidden", Boolean(token));
  $("#appPanel").classList.toggle("hidden", !token);
  render();
}

function render() {
  const ventas = data();
  const filtro = $("#estado")?.value || "TODAS";
  const rows = ventas.filter((v) => filtro === "TODAS" || v.estado === filtro);
  if ($("#total")) {
    $("#total").textContent = ventas.length;
    $("#activas").textContent = ventas.filter((v) => v.estado === "ACTIVA").length;
    $("#anuladas").textContent = ventas.filter((v) => v.estado === "ANULADA").length;
  }
  if (!$("#tbody")) return;
  $("#tbody").innerHTML = rows.map((v) => `<tr><td>${v.id}</td><td>${v.cliente}</td><td>${v.producto} × ${v.cantidad}</td><td>${v.total.toFixed(2)}</td><td>${v.estado}</td><td><button data-id="${v.id}" ${v.estado !== "ACTIVA" ? "disabled" : ""}>Anular</button></td></tr>`).join("") || '<tr><td colspan="6">Sin datos</td></tr>';
}

$("#loginBtn").onclick = () => {
  if ($("#user").value === "admin" && $("#pass").value === "admin123") {
    token = "jwt-demo"; sessionStorage.setItem("token", token); $("#msg").textContent = "";
  } else $("#msg").textContent = "Credenciales inválidas";
  guard();
};
$("#logoutBtn").onclick = () => { token = ""; sessionStorage.removeItem("token"); guard(); };
$("#crearBtn").onclick = () => {
  const option = $("#producto").selectedOptions[0];
  const cantidad = Number($("#cantidad").value);
  const response = api(() => {
    if (!$("#cliente").value.trim() || !Number.isInteger(cantidad) || cantidad <= 0) return { ok: false, error: "Datos inválidos" };
    const ventas = data(); const precio = Number(option.dataset.precio);
    ventas.push({ id: `V-${Date.now()}`, cliente: $("#cliente").value.trim(), producto: option.value, cantidad, total: cantidad * precio, estado: "ACTIVA", usuario: "admin" });
    save(ventas); return { ok: true };
  });
  $("#formMsg").textContent = response.ok ? "Venta registrada" : response.error; render();
};
$("#estado").onchange = render;
$("#tbody").onclick = (event) => {
  const button = event.target.closest("button[data-id]"); if (!button) return;
  api(() => { save(data().map((v) => v.id === button.dataset.id ? { ...v, estado: "ANULADA" } : v)); return { ok: true }; }); render();
};
if (data().length === 0) save([{ id: "V-1001", cliente: "Ana Torres", producto: "Mochila urbana", cantidad: 1, total: 89.9, estado: "ACTIVA", usuario: "admin" }]);
guard();
