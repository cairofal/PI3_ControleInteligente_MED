import { Link } from 'react-router-dom';

function Header({ isLoggedIn, handleLogout }) {
  return (
    <header style={styles.header}>
      <nav style={styles.nav}>
        <Link to="/" style={styles.logo}>Controle Inteligente para Tratamento Médico</Link>
        <div>
          {isLoggedIn ? (
            <>
              <Link to="/dashboard" style={styles.link}>Dashboard</Link>
              <button onClick={handleLogout} style={styles.button}>Sair</button>
            </>
          ) : (
            <Link to="/login" style={styles.button}>Login</Link>
          )}
        </div>
      </nav>
    </header>
  );
}

const styles = {
  header: {
    backgroundColor: '#282c34',
    padding: '1rem',
    color: 'white',
  },
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    maxWidth: '1200px',
    margin: '0 auto',
  },
  logo: {
    color: 'white',
    textDecoration: 'none',
    fontSize: '1.5rem',
    fontWeight: 'bold',
  },
  link: {
    color: 'white',
    textDecoration: 'none',
    marginRight: '1rem',
  },
  button: {
    backgroundColor: '#61dafb',
    color: '#282c34',
    padding: '0.5rem 1rem',
    borderRadius: '4px',
    textDecoration: 'none',
    border: 'none',
    cursor: 'pointer',
    fontWeight: 'bold',
  }
};

export default Header;