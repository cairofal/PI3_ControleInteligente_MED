/* eslint-disable no-undef */
import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import Header from './components/Header';
import LoginForm from './components/LoginForm';
import MedicamentosCadastrados from './pages/MedicamentosCadastrados';
import Pacientes from './pages/Pacientes';
import Medicamentos from './pages/Medicamentos';
import Lembretes from './pages/Lembretes';
import Consultas from './pages/Consultas';
import Exames from './pages/Exames';
import Medicos from './pages/Medicos';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
  };

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />} />
        <Route path="/dashboard" element={
          isLoggedIn ? 
            <Dashboard isLoggedIn={isLoggedIn} handleLogout={handleLogout} user={"usuario@exemplo.com"} /> : 
            <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/login" element={
          <>
            <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
            <LoginForm handleLogin={handleLogin} />
          </>
        } />
        
        {/* Novas rotas */}
        <Route path="/pacientes" element={
          isLoggedIn ? <Pacientes isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/medicamentos" element={
          isLoggedIn ? <Medicamentos isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/lembretes" element={
          isLoggedIn ? <Lembretes isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/consultas" element={
          isLoggedIn ? <Consultas isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/exames" element={
          isLoggedIn ? <Exames isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/medicos" element={
          isLoggedIn ? <Medicos isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
        <Route path="/medicamentos-cadastrados" element={
          isLoggedIn ? <MedicamentosCadastrados isLoggedIn={isLoggedIn} handleLogout={handleLogout} /> : <Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
        } />
      </Routes>
    </Router>
  );
}

export default App;