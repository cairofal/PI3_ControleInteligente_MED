import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import Header from './components/Header';
import LoginForm from './components/LoginForm';

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
        <Route 
          path="/" 
          element={<Home isLoggedIn={isLoggedIn} handleLogout={handleLogout} />} 
        />
        <Route 
          path="/dashboard" 
          element={<Dashboard isLoggedIn={isLoggedIn} handleLogout={handleLogout} />} 
        />
        <Route 
          path="/login" 
          element={
            <>
              <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
              <LoginForm handleLogin={handleLogin} />
            </>
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;