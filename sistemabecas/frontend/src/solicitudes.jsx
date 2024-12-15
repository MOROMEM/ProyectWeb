import { useState, useEffect } from 'react';
import './Solicitudes.css';
import PropTypes from 'prop-types';

function Solicitudes({ onLogout }) {
    const [solicitudes, setSolicitudes] = useState([]);
    const [filteredSolicitudes, setFilteredSolicitudes] = useState([]); // Nueva lista filtrada
    const [descripcion, setDescripcion] = useState('');
    const [estado, setEstado] = useState('pendiente');
    const [editingId, setEditingId] = useState(null);
    const [message, setMessage] = useState('');
    const [search, setSearch] = useState(''); // Filtro de búsqueda por nombre
    const [filterEstado, setFilterEstado] = useState(''); // Filtro por estado

    const token = localStorage.getItem('token');
    const isAdmin = localStorage.getItem('admin') === 'true';
    const userId = localStorage.getItem('userId');

    // Fetch solicitudes
    useEffect(() => {
        const fetchSolicitudes = async () => {
            if (!token) {
                setMessage('Token no encontrado. Por favor, inicia sesión nuevamente.');
                return;
            }

            try {
                const url = isAdmin
                    ? 'http://localhost:8080/solicitudes'
                    : `http://localhost:8080/solicitudes/usuario/${userId}`;

                const response = await fetch(url, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setSolicitudes(data);
                    setFilteredSolicitudes(data); // Inicializar la lista filtrada
                } else {
                    const errorText = await response.text();
                    setMessage(`Error al obtener las solicitudes: ${errorText}`);
                }
            } catch (error) {
                console.error('Error al conectar con el servidor:', error);
                setMessage('Error al conectar con el servidor.');
            }
        };

        fetchSolicitudes();
    }, [isAdmin, token, userId]);

    // Filtrar solicitudes dinámicamente por búsqueda y estado
    useEffect(() => {
        let result = solicitudes;

        // Filtrar por búsqueda (nombre/descripcion)
        if (search.trim() !== '') {
            result = result.filter((solicitud) =>
                solicitud.descripcion.toLowerCase().includes(search.toLowerCase())
            );
        }

        // Filtrar por estado
        if (filterEstado !== '') {
            result = result.filter((solicitud) => solicitud.estado === filterEstado);
        }

        setFilteredSolicitudes(result);
    }, [search, filterEstado, solicitudes]);

    // Crear solicitud
    const handleCreate = async (e) => {
        e.preventDefault();

        if (!descripcion.trim()) {
            setMessage('La descripción no puede estar vacía.');
            return;
        }

        const data = { descripcion }; // Solo enviamos la descripción

        try {
            const response = await fetch('http://localhost:8080/solicitudes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Enviar el token para extraer usuarioId
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                const newSolicitud = await response.json();
                setSolicitudes((prev) => [...prev, newSolicitud]);
                setDescripcion('');
                setMessage('Solicitud creada exitosamente.');
            } else {
                const errorText = await response.text();
                setMessage(`Error al crear la solicitud: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al conectar con el servidor:', error);
            setMessage('Error al conectar con el servidor.');
        }
    };

    // Eliminar solicitud (Solo para admin)
    const handleDelete = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/solicitudes/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`, // Token necesario para autorización
                },
            });

            if (response.ok) {
                setSolicitudes((prev) => prev.filter((solicitud) => solicitud.id !== id));
                setMessage('Solicitud eliminada exitosamente.');
            } else {
                const errorText = await response.text();
                setMessage(`Error al eliminar la solicitud: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al conectar con el servidor:', error);
            setMessage('Error al conectar con el servidor.');
        }
    };

    // Actualizar solicitud (Solo para admin)
    const handleUpdate = async (e) => {
        e.preventDefault();

        if (!descripcion.trim()) {
            setMessage('La descripción no puede estar vacía.');
            return;
        }

        const data = { descripcion, estado }; // Datos actualizados de la solicitud

        try {
            const response = await fetch(`http://localhost:8080/solicitudes/${editingId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Token necesario para autorización
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                const updatedSolicitud = await response.json();
                setSolicitudes((prev) =>
                    prev.map((solicitud) => (solicitud.id === editingId ? updatedSolicitud : solicitud))
                );
                setEditingId(null); // Limpiar edición
                setDescripcion('');
                setEstado('pendiente');
                setMessage('Solicitud actualizada exitosamente.');
            } else {
                const errorText = await response.text();
                setMessage(`Error al actualizar la solicitud: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al conectar con el servidor:', error);
            setMessage('Error al conectar con el servidor.');
        }
    };



    return (
        <div className="Solicitudes">
            <h1>Gestión de Solicitudes</h1>

            <button onClick={onLogout} className="logout-button">
                Cerrar Sesión
            </button>

            {message && <p className="message">{message}</p>}

            {/* Filtros */}
            <div className="filters">
                <select value={filterEstado} onChange={(e) => setFilterEstado(e.target.value)}>
                    <option value="">Filtrar por estado</option>
                    <option value="pendiente">Pendiente</option>
                    <option value="aprobado">Aprobado</option>
                    <option value="rechazado">Rechazado</option>
                </select>
            </div>

            {/* Formulario para crear o actualizar solicitudes */}
            <form onSubmit={editingId ? handleUpdate : handleCreate}>
                <input
                    type="text"
                    placeholder="Descripción de la solicitud"
                    value={descripcion}
                    onChange={(e) => setDescripcion(e.target.value)}
                    required
                />
                {isAdmin && editingId && (
                    <select value={estado} onChange={(e) => setEstado(e.target.value)}>
                        <option value="pendiente">Pendiente</option>
                        <option value="aprobado">Aprobado</option>
                        <option value="rechazado">Rechazado</option>
                    </select>
                )}
                <button type="submit">{editingId ? 'Actualizar' : 'Crear'}</button>
            </form>

            <h2>Lista de Solicitudes</h2>
            {filteredSolicitudes.length === 0 ? (
                <p>No se encontraron solicitudes.</p>
            ) : (
                <ul>
                    {filteredSolicitudes.map((solicitud) => (
                        <li key={solicitud.id}>
                            <p>
                                <strong>Descripción:</strong> {solicitud.descripcion}
                            </p>
                            <p>
                                <strong>Estado:</strong> {solicitud.estado}
                            </p>
                            {isAdmin && (
                                <>
                                    <button
                                        onClick={() => {
                                            setEditingId(solicitud.id);
                                            setDescripcion(solicitud.descripcion);
                                            setEstado(solicitud.estado);
                                        }}
                                    >
                                        Editar
                                    </button>
                                    <button onClick={() => handleDelete(solicitud.id)}>
                                        Eliminar
                                    </button>
                                </>
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

Solicitudes.propTypes = {
    onLogout: PropTypes.func.isRequired,
};

export default Solicitudes;
