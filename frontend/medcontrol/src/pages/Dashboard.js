import Header from '../components/Header';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';

function Dashboard({ isLoggedIn, handleLogout, user }) {
  const navigate = useNavigate();

  if (!isLoggedIn) {
    return <p>Redirecionando para login...</p>;
  }

  // Dados dos cards do dashboard
  const features = [
    {
      id: 1,
      title: 'Cadastro de Pacientes',
      icon: '👨‍⚕️',
      path: '/pacientes'
    },
    {
      id: 2,
      title: 'Cadastro de Medicamentos',
      icon: '💊',
      path: '/medicamentos'
    },
    {
      id: 3,
      title: 'Lembrete de Medicamentos',
      icon: '⏰',
      path: '/lembretes'
    },
    {
      id: 4,
      title: 'Agenda de Consultas',
      icon: '📅',
      path: '/consultas'
    },
    {
      id: 5,
      title: 'Agenda de Exames',
      icon: '🏥',
      path: '/exames'
    },
    {
      id: 6,
      title: 'Lista de Médicos',
      icon: '👩‍⚕️',
      path: '/medicos'
    },
    {
      id: 7,
      title: 'Medicamentos Cadastrados',
      icon: '📋',
      path: '/medicamentos-cadastrados'
    },
    {
    id: 8,
    title: 'Aferir Glicemia e Pressão',
    icon: '🩸',
    path: '/afericoes'
  }
  ];

  const handleCardClick = (path) => {
    // Aqui você pode implementar a navegação para cada funcionalidade
    navigate(path);
    // Ou mostrar um modal/alerta se a funcionalidade não estiver implementada
    // alert(`Funcionalidade ${title} será implementada em breve!`);
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <h1 style={styles.title}>Bem-vindo {user?.email}!</h1>
        <p style={styles.subtitle}>Controle Inteligente de Tratamentos Médicos</p>
        
        <div style={styles.gridContainer}>
          {features.map((feature) => (
            <div 
              key={feature.id} 
              style={styles.card}
              onClick={() => handleCardClick(feature.path)}
            >
              <div style={styles.cardIcon}>{feature.icon}</div>
              <h3 style={styles.cardTitle}>{feature.title}</h3>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

// Estilos atualizados
const styles = {
  main: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '2rem',
  },
  title: {
    fontSize: '2rem',
    marginBottom: '0.5rem',
    color: '#282c34',
    textAlign: 'center',
  },
  subtitle: {
    fontSize: '1.2rem',
    color: '#666',
    textAlign: 'center',
    marginBottom: '2rem',
  },
  gridContainer: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
    gap: '1.5rem',
    padding: '1rem',
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
    padding: '1.5rem',
    textAlign: 'center',
    cursor: 'pointer',
    transition: 'transform 0.2s, box-shadow 0.2s',
    '&:hover': {
      transform: 'translateY(-5px)',
      boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)',
    },
  },
  cardIcon: {
    fontSize: '3rem',
    marginBottom: '1rem',
  },
  cardTitle: {
    fontSize: '1.1rem',
    color: '#333',
    margin: '0',
  },
};

export default Dashboard;