const STORAGE_KEY = "comarket_erp_u1_ventas";
const Api = {
  ok: (status, data) => ({ status, ok: true, data }),
  error: (status, error) => ({ status, ok: false, error })
};

const VentaRepository = {
  listar: () => JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]"),
  guardar(ventas) { localStorage.setItem(STORAGE_KEY, JSON.stringify(ventas)); },
  agregar(venta) { const ventas = this.listar(); ventas.push(venta); this.guardar(ventas); },
  anular(id) { this.guardar(this.listar().map((v) => v.id === id ? { ...v, estado: "ANULADA", auditado: true } : v)); },
  limpiar() { localStorage.removeItem(STORAGE_KEY); }
};

const VentaService = {
  crear(dto) {
    if (!dto.cliente) return Api.error(400, "El cliente es obligatorio.");
    if (!Number.isInteger(dto.cantidad) || dto.cantidad <= 0) return Api.error(400, "La cantidad debe ser mayor que cero.");
    const detalle = { producto: dto.producto, cantidad: dto.cantidad, precioUnitario: dto.precio, subtotal: dto.cantidad * dto.precio };
    const venta = { id: `V-${Date.now()}`, cliente: dto.cliente, detalles: [detalle], total: detalle.subtotal, estado: "ACTIVA", auditado: false };
    VentaRepository.agregar(venta);
    return Api.ok(201, venta);
  },
  listar(estado) {
    return Api.ok(200, VentaRepository.listar().filter((v) => estado === "TODAS" || v.estado === estado));
  },
  anular(id) {
    const venta = VentaRepository.listar().find((v) => v.id === id);
    if (!venta) return Api.error(404, "La venta no existe.");
    if (venta.estado !== "ACTIVA") return Api.error(409, "La venta ya está anulada.");
    VentaRepository.anular(id);
    return Api.ok(200, { id, estado: "ANULADA", auditoria: "trg_venta_estado_audit" });
  }
};

const View = {
  mostrar(response) { document.querySelector("#responseBox").textContent = JSON.stringify(response, null, 2); this.render(); },
  render() {
    const ventas = VentaRepository.listar();
    document.querySelector("#ventasBody").innerHTML = ventas.length === 0
      ? '<tr><td colspan="6">No hay ventas.</td></tr>'
      : ventas.map((v) => `<tr><td>${v.id}</td><td>${v.cliente}</td><td>${v.detalles.map((d) => `${d.producto} × ${d.cantidad}`).join("<br>")}</td><td>${v.total.toFixed(2)}</td><td><span class="badge ${v.estado}">${v.estado}</span></td><td><button data-id="${v.id}" ${v.estado !== "ACTIVA" ? "disabled" : ""}>Anular</button></td></tr>`).join("");
  }
};

document.querySelector("#ventaForm").addEventListener("submit", (event) => {
  event.preventDefault();
  const option = document.querySelector("#producto").selectedOptions[0];
  View.mostrar(VentaService.crear({ cliente: document.querySelector("#cliente").value.trim(), producto: option.value, precio: Number(option.dataset.precio), cantidad: Number(document.querySelector("#cantidad").value) }));
});
document.querySelector("#getVentasBtn").addEventListener("click", () => View.mostrar(VentaService.listar(document.querySelector("#filtroEstado").value)));
document.querySelector("#seedBtn").addEventListener("click", () => { VentaRepository.guardar([{ id: "V-1001", cliente: "Ana Torres", detalles: [{ producto: "Mochila urbana", cantidad: 1, precioUnitario: 89.9, subtotal: 89.9 }], total: 89.9, estado: "ACTIVA", auditado: false }]); View.mostrar(Api.ok(200, "Datos cargados.")); });
document.querySelector("#clearBtn").addEventListener("click", () => { VentaRepository.limpiar(); View.mostrar(Api.ok(200, "Datos eliminados.")); });
document.querySelector("#ventasBody").addEventListener("click", (event) => { const button = event.target.closest("button[data-id]"); if (button) View.mostrar(VentaService.anular(button.dataset.id)); });
View.render();
