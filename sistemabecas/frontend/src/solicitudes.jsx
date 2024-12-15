import { useState, useEffect } from 'react';
import './Solicitudes.css';
import PropTypes from 'prop-types';

function Solicitudes({ onLogout }) {
    const [solicitudes, setSolicitudes] = useState([]);
    const [descripcion, setDescripcion] = useState('');
    const [estado, setEstado] = useState('pendiente');
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
            const response = await fetch('http://localhost:8080/solicitudes', {
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
    }, []);

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
            const response = await fetch(`http://localhost:8080/solicitudes/${editingId}/estado`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ estado }),
            });

            if (response.ok) {
                const updatedSolicitud = await response.json();
                setSolicitudes((prev) =>
                    prev.map((solicitud) => (solicitud.id === editingId ? updatedSolicitud : solicitud))
                );
                setEditingId(null);
                setEstado('pendiente');
                setMessage('Estado de la solicitud actualizado exitosamente.');
            } else {
                const errorText = await response.text();
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
        // Filtrar solicitudes según el rol y la vista actual
        const filteredSolicitudes = solicitudes.filter((solicitud) => {
            if (isAdmin) {
                return true; // Administradores ven todas las solicitudes
            }

            const usuarioAsociado = solicitud.usuarios.some((usuario) => usuario.usuarioId === userId);

            if (showUserSolicitudes) {
                // En "Mis Solicitudes", mostrar solo las asociadas al usuario actual
                console.log(`"Mis Solicitudes" - Usuario Asociado: ${usuarioAsociado}, Solicitud:`, solicitud);
                return usuarioAsociado;
            }

            // En "Becas Disponibles", mostrar todas excepto las asociadas al usuario actual
            console.log(`"Becas Disponibles" - Usuario Asociado: ${usuarioAsociado}, Solicitud:`, solicitud);
            return !usuarioAsociado;
        });

        // Si no hay solicitudes después de filtrar
        if (filteredSolicitudes.length === 0) {
            console.log('No hay solicitudes para mostrar en la vista actual.');
        }

        // Renderizar solicitudes filtradas
        return filteredSolicitudes.map((solicitud) => (
            <li key={solicitud.id}>
                <p>
                    <strong>Descripción:</strong> {solicitud.descripcion}
                </p>
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

                {/* Botón para solicitar beca (solo usuarios en "Becas Disponibles") */}
                {!isAdmin && !showUserSolicitudes && (
                    <button onClick={() => handleSolicitarBeca(solicitud.id)}>
                        Solicitar Beca
                    </button>
                )}

                {/* Botones de administrador */}
                {isAdmin && (
                    <>
                        <button
                            onClick={() => {
                                setEditingId(solicitud.id);
                                setEstado('pendiente'); // Establecer estado inicial para editar
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
