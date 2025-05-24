import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config';
import Header from '../components/Header';

function MedicamentosCadastrados({ isLoggedIn, handleLogout }) {
  const [medicamentos, setMedicamentos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMedicamentos = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(`${API_BASE_URL}/medicamentos`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        setMedicamentos(response.data);
      } catch (error) {
        console.error('Erro ao buscar medicamentos:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMedicamentos();
  }, []);

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <h1 style={styles.title}>Medicamentos Cadastrados</h1>
        
        {loading ? (
          <p>Carregando...</p>
        ) : (
          <table style={styles.table}>
            <thead>
              <tr>
                <th>Nome</th>
                <th>Dosagem</th>
                <th>Frequência</th>
                <th>Indicações</th>
              </tr>
            </thead>
            <tbody>
              {medicamentos.map((med) => (
                <tr key={med.id}>
                  <td>{med.nome}</td>
                  <td>{med.dosagem}</td>
                  <td>{med.frequencia}</td>
                  <td>{med.indicacoes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
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
  title: {
    fontSize: '2rem',
    marginBottom: '1.5rem',
    color: '#282c34',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    marginTop: '1rem',
    '& th, & td': {
      border: '1px solid #ddd',
      padding: '8px',
      textAlign: 'left',
    },
    '& th': {
      backgroundColor: '#f2f2f2',
    },
    '& tr:nth-child(even)': {
      backgroundColor: '#f9f9f9',
    },
  },
};

export default MedicamentosCadastrados;