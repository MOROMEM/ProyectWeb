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
            const url = 'http://localhost:8080/solicitudes';
            const response = await fetch(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            console.log('Response status:', response.status);

            if (response.ok) {
                const data = await response.json();
                console.log('Solicitudes recibidas del backend:', data); // Log de las solicitudes
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


    // Llamar a fetchSolicitudes al montar el componente
    useEffect(() => {
        console.log('Debugging Authentication Details:');
        console.log('Token:', token);
        console.log('User ID:', userId);
        console.log('Is Admin:', isAdmin);

        fetchSolicitudes();
    }, []);

    // Crear solicitud (Solo para admin)
    const handleCreate = async (e) => {
        e.preventDefault();

        if (!descripcion.trim()) {
            setMessage('La descripción no puede estar vacía.');
            return;
        }

        const data = { descripcion };

        try {
            const response = await fetch('http://localhost:8080/solicitudes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
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

    // Actualizar estado de la solicitud (Solo para admin)
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

    // Eliminar solicitud (Solo para admin)
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

    // Solicitar beca (Usuario normal)
    const handleSolicitarBeca = async (solicitudId) => {
        try {
            const response = await fetch(`http://localhost:8080/solicitudes/${solicitudId}/asociar-usuario`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // El token incluye el usuario logueado
                },
                body: JSON.stringify({ estado: 'pendiente' }) // Solo enviar el estado si es necesario
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


    // Alternar entre "Mis Solicitudes" y "Becas Disponibles" para usuarios
    const toggleView = () => {
        setShowUserSolicitudes((prev) => !prev);
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
                <form onSubmit={handleCreate}>
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
            {solicitudes.length === 0 ? (
                <p>No hay solicitudes disponibles.</p>
            ) : (
                <ul>
                    {solicitudes
                        .filter((solicitud) => {
                            console.log('Solicitud usuarioId:', solicitud.usuarioId, 'Current userId:', userId);

                            if (isAdmin) return true;

                            if (showUserSolicitudes) {
                                return solicitud.usuarioId === userId; // Mis solicitudes
                            } else {
                                return !solicitud.usuarioId; // Becas disponibles
                            }
                        })


                        .map((solicitud) => (
                            <li key={solicitud.id}>
                                <p>
                                    <strong>Descripción:</strong> {solicitud.descripcion}
                                </p>
                                <p>
                                    <strong>Estado:</strong> {solicitud.estado}
                                </p>
                                {/* Botón para solicitar beca (usuarios normales, en vista de becas disponibles) */}
                                {!isAdmin && !showUserSolicitudes && !solicitud.usuarioId && (
                                    <button onClick={() => handleSolicitarBeca(solicitud.id)}>
                                        Solicitar Beca
                                    </button>
                                )}
                                {/* Acciones solo para administradores */}
                                {isAdmin && (
                                    <>
                                        <button
                                            onClick={() => {
                                                setEditingId(solicitud.id);
                                                setEstado(solicitud.estado);
                                            }}
                                        >
                                            Actualizar Estado
                                        </button>
                                        <button onClick={() => handleDelete(solicitud.id)}>Eliminar</button>
                                    </>
                                )}
                            </li>
                        ))}
                </ul>
            )}

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
