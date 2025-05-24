import Header from '../components/Header';

function Home({ isLoggedIn, handleLogout }) {
  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <section style={styles.hero}>
          <h1 style={styles.heroTitle}>Bem-vindo(a) ao sistema de Controle Inteligente para Tratamentos Médicos</h1>
          <p style={styles.heroText}>
            Solução completa para o acompanhamento de tratamentos médicos. Faça login para acessar sua conta.
          </p>
        </section>
      </main>
    </div>
  );
}

const styles = {
  main: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '2rem',
  },
  hero: {
    textAlign: 'center',
    padding: '4rem 0',
  },
  heroTitle: {
    fontSize: '2.5rem',
    marginBottom: '1rem',
    color: '#282c34',
  },
  heroText: {
    fontSize: '1.2rem',
    color: '#666',
    maxWidth: '600px',
    margin: '0 auto',
  }
};

export default Home;