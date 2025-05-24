import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config';
import Header from '../components/Header';

function Medicamentos({ isLoggedIn, handleLogout }) {
  const [medicamentos, setMedicamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    nome: '',
    principioAtivo: '',
    dosagem: '',
    formaFarmaceutica: 'comprimido',
    laboratorio: '',
    indicacoes: '',
    contraIndicacoes: ''
  });
  const [editingId, setEditingId] = useState(null);

  // Opções para o dropdown de forma farmacêutica
  const formasFarmaceuticas = [
    'comprimido',
    'cápsula',
    'líquido',
    'pomada',
    'creme',
    'gel',
    'injetável',
    'spray',
    'supositório'
  ];

  useEffect(() => {
    fetchMedicamentos();
  }, []);

  const fetchMedicamentos = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/medicamentos`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setMedicamentos(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Erro ao buscar medicamentos:', error);
      setError('Erro ao carregar medicamentos');
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      
      if (editingId) {
        // Atualizar medicamento existente
        await axios.put(`${API_BASE_URL}/medicamentos/${editingId}`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      } else {
        // Criar novo medicamento
        await axios.post(`${API_BASE_URL}/medicamentos`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      }
      
      // Resetar formulário e recarregar lista
      setShowForm(false);
      setFormData({
        nome: '',
        principioAtivo: '',
        dosagem: '',
        formaFarmaceutica: 'comprimido',
        laboratorio: '',
        indicacoes: '',
        contraIndicacoes: ''
      });
      setEditingId(null);
      fetchMedicamentos();
    } catch (error) {
      console.error('Erro ao salvar medicamento:', error);
      setError('Erro ao salvar medicamento');
    }
  };

  const handleEdit = (medicamento) => {
    setFormData({
      nome: medicamento.nome,
      principioAtivo: medicamento.principioAtivo,
      dosagem: medicamento.dosagem,
      formaFarmaceutica: medicamento.formaFarmaceutica,
      laboratorio: medicamento.laboratorio,
      indicacoes: medicamento.indicacoes,
      contraIndicacoes: medicamento.contraIndicacoes
    });
    setEditingId(medicamento.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este medicamento?')) {
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`${API_BASE_URL}/medicamentos/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        fetchMedicamentos();
      } catch (error) {
        console.error('Erro ao excluir medicamento:', error);
        setError('Erro ao excluir medicamento');
      }
    }
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <h1 style={styles.title}>Cadastro de Medicamentos</h1>
        
        <button 
          style={styles.addButton}
          onClick={() => {
            setShowForm(!showForm);
            if (showForm) {
              setEditingId(null);
              setFormData({
                nome: '',
                principioAtivo: '',
                dosagem: '',
                formaFarmaceutica: 'comprimido',
                laboratorio: '',
                indicacoes: '',
                contraIndicacoes: ''
              });
            }
          }}
        >
          {showForm ? 'Cancelar' : '+ Adicionar Medicamento'}
        </button>

        {showForm && (
          <form style={styles.form} onSubmit={handleSubmit}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Nome Comercial:</label>
              <input
                type="text"
                name="nome"
                value={formData.nome}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Princípio Ativo:</label>
              <input
                type="text"
                name="principioAtivo"
                value={formData.principioAtivo}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Dosagem:</label>
              <input
                type="text"
                name="dosagem"
                value={formData.dosagem}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="Ex: 50mg, 100mg/ml"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Forma Farmacêutica:</label>
              <select
                name="formaFarmaceutica"
                value={formData.formaFarmaceutica}
                onChange={handleInputChange}
                style={styles.input}
              >
                {formasFarmaceuticas.map((forma) => (
                  <option key={forma} value={forma}>
                    {forma.charAt(0).toUpperCase() + forma.slice(1)}
                  </option>
                ))}
              </select>
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Laboratório:</label>
              <input
                type="text"
                name="laboratorio"
                value={formData.laboratorio}
                onChange={handleInputChange}
                style={styles.input}
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Indicações:</label>
              <textarea
                name="indicacoes"
                value={formData.indicacoes}
                onChange={handleInputChange}
                style={{...styles.input, minHeight: '80px'}}
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Contraindicações:</label>
              <textarea
                name="contraIndicacoes"
                value={formData.contraIndicacoes}
                onChange={handleInputChange}
                style={{...styles.input, minHeight: '80px'}}
              />
            </div>
            
            <button type="submit" style={styles.submitButton}>
              {editingId ? 'Atualizar' : 'Cadastrar'}
            </button>
          </form>
        )}

        {error && <p style={styles.error}>{error}</p>}

        {loading ? (
          <p>Carregando...</p>
        ) : (
          <div style={styles.tableContainer}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th>Nome Comercial</th>
                  <th>Princípio Ativo</th>
                  <th>Dosagem</th>
                  <th>Forma</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {medicamentos.length > 0 ? (
                  medicamentos.map((medicamento) => (
                    <tr key={medicamento.id}>
                      <td>{medicamento.nome}</td>
                      <td>{medicamento.principioAtivo}</td>
                      <td>{medicamento.dosagem}</td>
                      <td>{medicamento.formaFarmaceutica}</td>
                      <td style={styles.actions}>
                        <button 
                          style={styles.editButton}
                          onClick={() => handleEdit(medicamento)}
                        >
                          Editar
                        </button>
                        <button 
                          style={styles.deleteButton}
                          onClick={() => handleDelete(medicamento.id)}
                        >
                          Excluir
                        </button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="5" style={styles.noData}>Nenhum medicamento cadastrado</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}

// Estilos (podem ser os mesmos do Pacientes.js com pequenos ajustes)
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
  addButton: {
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    padding: '10px 15px',
    borderRadius: '4px',
    cursor: 'pointer',
    marginBottom: '20px',
    fontSize: '1rem',
    fontWeight: 'bold',
    '&:hover': {
      backgroundColor: '#45a049',
    },
  },
  form: {
    backgroundColor: '#f9f9f9',
    padding: '20px',
    borderRadius: '8px',
    marginBottom: '20px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  formGroup: {
    marginBottom: '15px',
  },
  label: {
    display: 'block',
    marginBottom: '5px',
    fontWeight: 'bold',
    color: '#555',
  },
  input: {
    width: '100%',
    padding: '8px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
  },
  submitButton: {
    backgroundColor: '#2196F3',
    color: 'white',
    border: 'none',
    padding: '10px 15px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem',
    '&:hover': {
      backgroundColor: '#0b7dda',
    },
  },
  error: {
    color: 'red',
    margin: '10px 0',
  },
  tableContainer: {
    overflowX: 'auto',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    marginTop: '1rem',
  },
  'table th, table td': {
    border: '1px solid #ddd',
    padding: '12px',
    textAlign: 'left',
  },
  'table th': {
    backgroundColor: '#f2f2f2',
    fontWeight: 'bold',
  },
  'table tr:nth-child(even)': {
    backgroundColor: '#f9f9f9',
  },
  'table tr:hover': {
    backgroundColor: '#f1f1f1',
  },
  actions: {
    display: 'flex',
    gap: '8px',
  },
  editButton: {
    backgroundColor: '#FFC107',
    color: '#000',
    border: 'none',
    padding: '5px 10px',
    borderRadius: '4px',
    cursor: 'pointer',
    '&:hover': {
      backgroundColor: '#e0a800',
    },
  },
  deleteButton: {
    backgroundColor: '#F44336',
    color: 'white',
    border: 'none',
    padding: '5px 10px',
    borderRadius: '4px',
    cursor: 'pointer',
    '&:hover': {
      backgroundColor: '#d32f2f',
    },
  },
  noData: {
    textAlign: 'center',
    padding: '20px',
    color: '#777',
  },
};

export default Medicamentos;