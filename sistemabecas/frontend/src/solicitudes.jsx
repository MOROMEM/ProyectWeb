import { useState, useEffect } from 'react';
import './Solicitudes.css';
import PropTypes from 'prop-types';

function Solicitudes({ onLogout }) {
    const [solicitudes, setSolicitudes] = useState([]);
    const [descripcion, setDescripcion] = useState('');
    const [estado, setEstado] = useState('pendiente');
    const [selectedUsuarioId, setSelectedUsuarioId] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [message, setMessage] = useState('');
    const [showUserSolicitudes, setShowUserSolicitudes] = useState(false);

    const token = localStorage.getItem('token');
    const isAdmin = localStorage.getItem('admin') === 'true';
    const userId = localStorage.getItem('userId');

    // Fetch solicitudes desde la API
    const fetchSolicitudes = async () => {
        if (!token) {
            setMessage('Token no encontrado. Por favor, inicia sesión nuevamente.');
            return;
        }

        try {
            let url = 'http://localhost:8080/solicitudes/';
            if (isAdmin) {
                url += 'admin/todas';
            } else if (showUserSolicitudes) {
                url += 'usuario/asociadas';
            } else {
                url += 'usuario/no-asociadas';
            }

            const response = await fetch(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            console.log('Token enviado:', token); // Debugging
            if (response.ok) {
                const data = await response.json();
                setSolicitudes(data);
            } else {
                const errorText = await response.text();
                console.log('Error al obtener solicitudes:', errorText);
                setMessage(`Error al obtener las solicitudes: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al conectar con el servidor:', error);
            setMessage('Error al conectar con el servidor.');
        }
    };

    useEffect(() => {
        console.log('Debugging Authentication Details:');
        console.log('Token:', token);
        console.log('User ID:', userId);
        console.log('Is Admin:', isAdmin);

        fetchSolicitudes();
    }, [isAdmin, showUserSolicitudes]); // actualizar cuando eliminas o actualizas

    const handleCreateSolicitud = async () => {
        try {
            const response = await fetch('http://localhost:8080/solicitudes/crear', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ descripcion }),
            });

            if (response.ok) {
                const newSolicitud = await response.json();
                setSolicitudes((prev) => [...prev, newSolicitud]);
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

    const handleUpdateEstado = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch(
                `http://localhost:8080/solicitudes/${editingId}/usuarios/${selectedUsuarioId}/estado`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify({ estado }),
                }
            );

            if (response.ok) {
                console.log('Estado actualizado exitosamente.');
                fetchSolicitudes(); // Refresca las solicitudes
            } else {
                const errorText = await response.text();
                console.error('Error al actualizar el estado:', errorText);
                setMessage(`Error al actualizar el estado: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al conectar con el servidor:', error);
            setMessage('Error al conectar con el servidor.');
        }
    };

    const handleDelete = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/solicitudes/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
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

    const handleSolicitarBeca = async (solicitudId) => {
        try {
            const response = await fetch(`http://localhost:8080/solicitudes/${solicitudId}/asociar-usuario`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                const updatedSolicitud = await response.json();
                setSolicitudes((prev) =>
                    prev.map((solicitud) => (solicitud.id === solicitudId ? updatedSolicitud : solicitud))
                );
                setMessage('Beca solicitada exitosamente.');
            } else {
                const errorText = await response.text();
                setMessage(`Error al solicitar beca: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al conectar con el servidor:', error);
            setMessage('Error al conectar con el servidor.');
        }
    };

    const toggleView = () => {
        setShowUserSolicitudes((prev) => !prev);
    };

    const renderSolicitudes = () => {
        return solicitudes.map((solicitud) => (
            <li key={solicitud.id}>
                <p>
                    <strong>Descripción:</strong> {solicitud.descripcion}
                </p>

                {/* Mostrar estados solo en 'Mis Solicitudes' */}
                {!isAdmin && showUserSolicitudes && (
                    <p>
                        <strong>Estado:</strong>{" "}
                        {solicitud.usuarios
                            .filter((usuarioItem) => usuarioItem.usuario.id === userId)
                            .map((usuarioItem) => usuarioItem.estado)
                            .join(", ") || "No asignado"}
                    </p>


                )}

                {/* Mostrar usuarios asociados solo para admin */}
                {isAdmin && (
                    <p>
                        <strong>Usuarios Asociados:</strong>
                        <ul>
                            {solicitud.usuarios.map((usuario) => (
                                <li key={usuario.usuarioId}>
                                    Usuario: {usuario.nombre} (ID: {usuario.usuarioId}) - Estado: {usuario.estado}
                                </li>
                            ))}
                        </ul>
                    </p>
                )}

                {/* Botón de "Solicitar Beca" para usuarios en "Becas Disponibles" */}
                {!isAdmin && !showUserSolicitudes && (
                    <button onClick={() => handleSolicitarBeca(solicitud.id)}>Solicitar Beca</button>
                )}

                {/* Opciones de admin */}
                {isAdmin && (
                    <>
                        <button
                            onClick={() => {
                                setEditingId(solicitud.id);
                                setEstado('pendiente');
                            }}
                        >
                            Actualizar Estado
                        </button>
                        <button onClick={() => handleDelete(solicitud.id)}>Eliminar</button>
                    </>
                )}
            </li>

        ));
    };

    return (
        <div className="Solicitudes">
            <h1>Gestión de Solicitudes</h1>
            <button onClick={onLogout} className="logout-button">
                Cerrar Sesión
            </button>
            {message && <p className="message">{message}</p>}
            {!isAdmin && (
                <button onClick={toggleView} className="toggle-view-button">
                    {showUserSolicitudes ? 'Ver Becas Disponibles' : 'Ver Mis Solicitudes'}
                </button>
            )}
            {isAdmin && (
                <form onSubmit={handleCreateSolicitud}>
                    <input
                        type="text"
                        placeholder="Descripción de la solicitud"
                        value={descripcion}
                        onChange={(e) => setDescripcion(e.target.value)}
                        required
                    />
                    <button type="submit">Crear Solicitud</button>
                </form>
            )}
            <h2>{isAdmin ? 'Lista de Solicitudes' : showUserSolicitudes ? 'Mis Solicitudes' : 'Becas Disponibles'}</h2>
            {solicitudes.length === 0 ? <p>No hay solicitudes disponibles.</p> : <ul>{renderSolicitudes()}</ul>}
            {isAdmin && editingId && (
                <form onSubmit={handleUpdateEstado}>
                    <select
                        value={selectedUsuarioId}
                        onChange={(e) => setSelectedUsuarioId(e.target.value)}
                    >
                        <option value="">Selecciona un usuario</option>
                        {solicitudes
                            .find((solicitud) => solicitud.id === editingId)
                            ?.usuarios.map((usuario) => (
                                <option key={usuario.usuarioId} value={usuario.usuarioId}>
                                    {usuario.nombre}
                                </option>
                            ))}
                    </select>

                    <select value={estado} onChange={(e) => setEstado(e.target.value)}>
                        <option value="pendiente">Pendiente</option>
                        <option value="aprobado">Aprobado</option>
                        <option value="rechazado">Rechazado</option>
                    </select>

                    <button type="submit">Guardar Cambios</button>
                </form>
            )}

        </div>
    );
}

Solicitudes.propTypes = {
    onLogout: PropTypes.func.isRequired,
};

export default Solicitudes;
