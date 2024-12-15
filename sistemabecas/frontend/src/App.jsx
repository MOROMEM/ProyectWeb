import { useState, useEffect } from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import Solicitudes from './Solicitudes'; // Importar el componente Solicitudes
import './App.css';

function App() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [nombre, setNombre] = useState('');
    const [message, setMessage] = useState('');
    const [showLogin, setShowLogin] = useState(true);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    // Verificar si el usuario está autenticado al cargar la aplicación
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            setIsLoggedIn(true); // Si hay un token, marcar como autenticado
        }
    }, []);

    const handleLogin = async (e) => {
        e.preventDefault();
        const data = { email, password };

        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                const result = await response.json();
                localStorage.setItem('token', result.token);
                localStorage.setItem('userId', result.userId);
                localStorage.setItem('admin', result.admin);
                setIsLoggedIn(true);
            } else {
                const errorData = await response.json();
                setMessage(errorData.message || 'Credenciales incorrectas.');
            }
        } catch {
            setMessage('Error al conectar con el servidor.');
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        const data = { nombre, email, password };

        try {
            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                setMessage('Registro exitoso. Ahora puedes iniciar sesión.');
                setShowLogin(true);
            } else {
                const errorData = await response.json();
                setMessage(errorData.message || 'Error al registrar usuario.');
            }
        } catch {
            setMessage('Error al conectar con el servidor.');
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('admin');
        setIsLoggedIn(false);
        setMessage('Sesión cerrada exitosamente.');
    };

    return (
        <Routes>
            <Route
                path="/"
                element={
                    isLoggedIn ? (
                        <Navigate to="/solicitudes" />
                    ) : (
                        <div className="App">
                            <h1>Sistema de Becas</h1>
                            {showLogin ? (
                                <div className="form-container">
                                    <h2>Login</h2>
                                    <form onSubmit={handleLogin}>
                                        <input
                                            type="email"
                                            placeholder="Correo Electrónico"
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            required
                                        />
                                        <input
                                            type="password"
                                            placeholder="Contraseña"
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                        />
                                        <button type="submit">Iniciar Sesión</button>
                                    </form>
                                    <p>
                                        ¿No tienes cuenta?{' '}
                                        <button className="toggle-button" onClick={() => setShowLogin(false)}>
                                            Regístrate
                                        </button>
                                    </p>
                                </div>
                            ) : (
                                <div className="form-container">
                                    <h2>Registro</h2>
                                    <form onSubmit={handleRegister}>
                                        <input
                                            type="text"
                                            placeholder="Nombre"
                                            value={nombre}
                                            onChange={(e) => setNombre(e.target.value)}
                                            required
                                        />
                                        <input
                                            type="email"
                                            placeholder="Correo Electrónico"
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            required
                                        />
                                        <input
                                            type="password"
                                            placeholder="Contraseña"
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                        />
                                        <button type="submit">Registrar</button>
                                    </form>
                                    <p>
                                        ¿Ya tienes cuenta?{' '}
                                        <button className="toggle-button" onClick={() => setShowLogin(true)}>
                                            Inicia Sesión
                                        </button>
                                    </p>
                                </div>
                            )}
                            {message && <p className="message">{message}</p>}
                        </div>
                    )
                }
            />
            <Route
                path="/solicitudes"
                element={
                    isLoggedIn ? (
                        <Solicitudes onLogout={handleLogout} />
                    ) : (
                        <Navigate to="/" />
                    )
                }
            />
        </Routes>
    );
}

export default App;




