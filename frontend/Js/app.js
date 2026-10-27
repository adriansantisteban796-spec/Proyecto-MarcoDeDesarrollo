// Utilidades compartidas por todas las paginas del sistema

// ---------- Sesion ----------
const Sesion = {
    token: () => localStorage.getItem('token') || '',
    correo: () => localStorage.getItem('usuarioActivo') || '',
    rol: () => localStorage.getItem('rolActivo') || '',
    esCoordinador: () => localStorage.getItem('rolActivo') === 'Coordinador',
    cerrar: function () {
        ['token', 'usuarioActivo', 'rolActivo'].forEach(k => localStorage.removeItem(k));
        window.location.href = 'Login.html';
    }
};

// Sin sesion no se muestra ninguna pagina
if (!Sesion.token() || !Sesion.correo()) {
    Sesion.cerrar();
}

// ---------- Llamadas a la API ----------
// Devuelve { ok, status, datos }. Si el token vencio, vuelve al login.
async function api(ruta, opciones = {}) {
    const cabeceras = { 'Authorization': 'Bearer ' + Sesion.token() };

    if (opciones.body !== undefined) {
        cabeceras['Content-Type'] = 'application/json';
    }

    let respuesta;

    try {
        respuesta = await fetch(ruta, {
            method: opciones.method || 'GET',
            headers: cabeceras,
            body: opciones.body !== undefined ? JSON.stringify(opciones.body) : undefined
        });
    } catch (e) {
        return { ok: false, status: 0, datos: 'No se pudo conectar con el servidor.' };
    }

    if (respuesta.status === 401) {
        Sesion.cerrar();
        return { ok: false, status: 401, datos: '' };
    }

    const texto = await respuesta.text();
    let datos = texto;

    try {
        datos = texto ? JSON.parse(texto) : null;
    } catch (e) {
        // La respuesta es texto simple (mensaje de error o de exito)
    }

    return { ok: respuesta.ok, status: respuesta.status, datos: datos };
}

// Mensaje para mostrar al usuario cuando una llamada falla
function mensajeError(r) {
    if (typeof r.datos === 'string' && r.datos) {
        return r.datos;
    }
    return 'No se pudo completar la operación (código ' + r.status + ').';
}

// ---------- Formato ----------
function escapar(texto) {
    const d = document.createElement('div');
    d.textContent = texto == null ? '' : texto;
    return d.innerHTML;
}

function fmtFecha(iso) {
    if (!iso) return '—';
    const f = new Date(iso);
    if (isNaN(f)) return escapar(iso);
    const dos = n => String(n).padStart(2, '0');
    return dos(f.getDate()) + '/' + dos(f.getMonth() + 1) + '/' + f.getFullYear()
        + ' ' + dos(f.getHours()) + ':' + dos(f.getMinutes());
}

// Fecha de hoy (hora local) como yyyy-MM-dd, para el atributo min de los campos de fecha
function hoyISO() {
    const h = new Date();
    const dos = n => String(n).padStart(2, '0');
    return h.getFullYear() + '-' + dos(h.getMonth() + 1) + '-' + dos(h.getDate());
}

// Aviso dentro de la pagina (reemplaza a los alert())
function mostrarAviso(id, texto, tipo) {
    const el = document.getElementById(id);
    el.className = 'alert alert-' + tipo;
    el.textContent = texto;
    el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// ---------- Catalogos (nombres de laboratorios y categorias) ----------
let _catalogos = null;

function cargarCatalogos() {
    if (!_catalogos) {
        _catalogos = api('/api/catalogos').then(r =>
            r.ok && r.datos ? r.datos : { laboratorios: [], categorias: [] });
    }
    return _catalogos;
}

function nombreLaboratorio(catalogos, id) {
    const lab = catalogos.laboratorios.find(l => String(l.id) === String(id));
    return lab ? lab.codigo : (id == null ? '—' : 'Lab ' + id);
}

function nombreCategoria(catalogos, id) {
    const cat = catalogos.categorias.find(c => String(c.id) === String(id));
    return cat ? cat.nombre : (id == null ? '—' : 'Cat. ' + id);
}

// Estados del equipo (tabla estado_equipo)
const ESTADOS_EQUIPO = {
    1: { nombre: 'Disponible', clase: 'bg-success', icono: 'bi-check-circle-fill' },
    2: { nombre: 'En Préstamo', clase: 'bg-warning text-dark', icono: 'bi-clock-history' },
    3: { nombre: 'En Mantenimiento', clase: 'bg-info text-dark', icono: 'bi-tools' },
    4: { nombre: 'Bloqueado', clase: 'bg-danger', icono: 'bi-x-octagon-fill' }
};

function badgeEstadoEquipo(id) {
    const e = ESTADOS_EQUIPO[id] || { nombre: 'Sin estado', clase: 'bg-secondary', icono: 'bi-question-circle' };
    return `<span class="badge rounded-pill ${e.clase} px-3 py-2"><i class="bi ${e.icono} me-1"></i>${e.nombre}</span>`;
}

// ---------- Cabecera y menu comunes ----------
function montarLayout(paginaActiva, subtitulo) {
    const enlaces = [
        ['inicio', 'boostrap.html', 'Inicio'],
        ['inventario', 'inventario.html', 'Inventario'],
        ['reservas', 'reservas.html', 'Reservas'],
        ['prestamos', 'prestamos.html', 'Préstamos'],
        ['mantenimiento', 'mantenimiento.html', 'Mantenimiento']
    ];

    if (Sesion.esCoordinador()) {
        enlaces.push(['usuarios', 'usuarios.html', 'Usuarios']);
    }

    const menu = enlaces.map(([id, href, texto]) =>
        `<a class="nav-link${id === paginaActiva ? ' active fw-bold' : ''}" href="${href}">${texto}</a>`
    ).join('');

    document.body.insertAdjacentHTML('afterbegin', `
        <nav class="bg-dark text-white py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <span class="small text-secondary">${escapar(subtitulo)}</span>
                <div class="d-flex align-items-center gap-3">
                    <span class="badge bg-primary px-3 py-2">Rol: ${escapar(Sesion.rol() || '—')}</span>
                    <span class="small text-light">${escapar(Sesion.correo())}</span>
                    <button class="btn btn-outline-danger btn-sm" id="btnCerrarSesion">
                        <i class="bi bi-box-arrow-right"></i> Salir
                    </button>
                </div>
            </div>
        </nav>
        <nav class="navbar navbar-expand-lg bg-body-tertiary border-bottom shadow-sm sticky-top">
            <div class="container-fluid">
                <a class="navbar-brand fw-bold text-primary" href="boostrap.html">
                    <i class="bi bi-cpu me-1"></i>Innova Schools
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#menuPrincipal">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="menuPrincipal">
                    <div class="navbar-nav ms-auto">${menu}</div>
                </div>
            </div>
        </nav>`);

    document.getElementById('btnCerrarSesion').addEventListener('click', Sesion.cerrar);
}
