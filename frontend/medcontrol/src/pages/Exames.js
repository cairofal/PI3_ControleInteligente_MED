import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config';
import Header from '../components/Header';

function Exames({ isLoggedIn, handleLogout }) {
  const [exames, setExames] = useState([]);
  const [medicos, setMedicos] = useState([]);
  const [tiposExame, setTiposExame] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    medicoId: '',
    tipoExame: '',
    dataHora: '',
    local: '',
    preparo: '',
    status: 'agendado',
    resultado: ''
  });
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    fetchExames();
    fetchMedicos();
    fetchTiposExame();
  }, []);

  const fetchExames = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/exames`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setExames(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Erro ao buscar exames:', error);
      setError('Erro ao carregar exames');
      setLoading(false);
    }
  };

  const fetchMedicos = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/medicos`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setMedicos(response.data);
    } catch (error) {
      console.error('Erro ao buscar médicos:', error);
    }
  };

  const fetchTiposExame = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/tipos-exame`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setTiposExame(response.data);
    } catch (error) {
      console.error('Erro ao buscar tipos de exame:', error);
      // Caso o endpoint não exista, usar uma lista padrão
      setTiposExame([
        'Hemograma completo',
        'Glicemia em jejum',
        'Colesterol total e frações',
        'Triglicerídeos',
        'TSH e T4 livre',
        'Urina tipo I',
        'Ureia e creatinina',
        'TGO/AST e TGP/ALT',
        'PSA',
        'Eletrocardiograma',
        'Ecocardiograma',
        'Ultrassonografia',
        'Ressonância magnética',
        'Tomografia computadorizada',
        'Raio-X'
      ]);
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
        // Atualizar exame existente
        await axios.put(`${API_BASE_URL}/exames/${editingId}`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      } else {
        // Criar novo exame
        await axios.post(`${API_BASE_URL}/exames`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      }
      
      // Resetar formulário e recarregar lista
      setShowForm(false);
      setFormData({
        medicoId: '',
        tipoExame: '',
        dataHora: '',
        local: '',
        preparo: '',
        status: 'agendado',
        resultado: ''
      });
      setEditingId(null);
      fetchExames();
    } catch (error) {
      console.error('Erro ao salvar exame:', error);
      setError('Erro ao salvar exame');
    }
  };

  const handleEdit = (exame) => {
    setFormData({
      medicoId: exame.medicoId,
      tipoExame: exame.tipoExame,
      dataHora: exame.dataHora.slice(0, 16), // Ajuste para input datetime-local
      local: exame.local,
      preparo: exame.preparo,
      status: exame.status,
      resultado: exame.resultado
    });
    setEditingId(exame.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja cancelar este exame?')) {
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`${API_BASE_URL}/exames/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        fetchExames();
      } catch (error) {
        console.error('Erro ao cancelar exame:', error);
        setError('Erro ao cancelar exame');
      }
    }
  };

  const handleStatusChange = async (id, novoStatus) => {
    try {
      const token = localStorage.getItem('token');
      await axios.patch(`${API_BASE_URL}/exames/${id}/status`, 
        { status: novoStatus },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      fetchExames();
    } catch (error) {
      console.error('Erro ao atualizar status:', error);
      setError('Erro ao atualizar status do exame');
    }
  };

  const handleResultadoChange = async (id, resultado) => {
    try {
      const token = localStorage.getItem('token');
      await axios.patch(`${API_BASE_URL}/exames/${id}/resultado`, 
        { resultado },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      fetchExames();
    } catch (error) {
      console.error('Erro ao atualizar resultado:', error);
      setError('Erro ao atualizar resultado do exame');
    }
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <h1 style={styles.title}>Agenda de Exames</h1>
        
        <button 
          style={styles.addButton}
          onClick={() => {
            setShowForm(!showForm);
            if (showForm) {
              setEditingId(null);
              setFormData({
                medicoId: '',
                tipoExame: '',
                dataHora: '',
                local: '',
                preparo: '',
                status: 'agendado',
                resultado: ''
              });
            }
          }}
        >
          {showForm ? 'Cancelar' : '+ Agendar Exame'}
        </button>

        {showForm && (
          <form style={styles.form} onSubmit={handleSubmit}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Médico Solicitante:</label>
              <select
                name="medicoId"
                value={formData.medicoId}
                onChange={handleInputChange}
                style={styles.input}
                required
              >
                <option value="">Selecione um médico</option>
                {medicos.map((medico) => (
                  <option key={medico.id} value={medico.id}>
                    Dr. {medico.nome} ({medico.especialidade})
                  </option>
                ))}
              </select>
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Tipo de Exame:</label>
              <input
                list="tiposExame"
                name="tipoExame"
                value={formData.tipoExame}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="Digite ou selecione o tipo de exame"
              />
              <datalist id="tiposExame">
                {tiposExame.map((tipo, index) => (
                  <option key={index} value={tipo} />
                ))}
              </datalist>
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Data e Hora:</label>
              <input
                type="datetime-local"
                name="dataHora"
                value={formData.dataHora}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Local:</label>
              <input
                type="text"
                name="local"
                value={formData.local}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="Ex: Laboratório X, Hospital Y"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Preparo:</label>
              <textarea
                name="preparo"
                value={formData.preparo}
                onChange={handleInputChange}
                style={{...styles.input, minHeight: '80px'}}
                placeholder="Jejum de 8 horas, suspender medicamento X, etc."
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Status:</label>
              <select
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                style={styles.input}
                required
              >
                <option value="agendado">Agendado</option>
                <option value="realizado">Realizado</option>
                <option value="cancelado">Cancelado</option>
              </select>
            </div>
            
            {formData.status === 'realizado' && (
              <div style={styles.formGroup}>
                <label style={styles.label}>Resultado:</label>
                <textarea
                  name="resultado"
                  value={formData.resultado}
                  onChange={handleInputChange}
                  style={{...styles.input, minHeight: '80px'}}
                  placeholder="Descrição do resultado ou laudo"
                />
              </div>
            )}
            
            <button type="submit" style={styles.submitButton}>
              {editingId ? 'Atualizar' : 'Agendar'}
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
                  <th>Tipo de Exame</th>
                  <th>Médico</th>
                  <th>Data/Hora</th>
                  <th>Local</th>
                  <th>Status</th>
                  <th>Resultado</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {exames.length > 0 ? (
                  exames.map((exame) => {
                    const medico = medicos.find(m => m.id === exame.medicoId);
                    return (
                      <tr key={exame.id}>
                        <td>{exame.tipoExame}</td>
                        <td>Dr. {medico?.nome || 'Médico não encontrado'}</td>
                        <td>{new Date(exame.dataHora).toLocaleString()}</td>
                        <td>{exame.local}</td>
                        <td>
                          <select
                            value={exame.status}
                            onChange={(e) => handleStatusChange(exame.id, e.target.value)}
                            style={{
                              ...styles.statusSelect,
                              backgroundColor: getStatusColor(exame.status)
                            }}
                          >
                            <option value="agendado">Agendado</option>
                            <option value="realizado">Realizado</option>
                            <option value="cancelado">Cancelado</option>
                          </select>
                        </td>
                        <td>
                          {exame.status === 'realizado' ? (
                            <textarea
                              value={exame.resultado || ''}
                              onChange={(e) => handleResultadoChange(exame.id, e.target.value)}
                              style={{...styles.resultadoInput, minHeight: '50px'}}
                              placeholder="Adicionar resultado"
                            />
                          ) : '-'}
                        </td>
                        <td style={styles.actions}>
                          <button 
                            style={styles.editButton}
                            onClick={() => handleEdit(exame)}
                          >
                            Editar
                          </button>
                          <button 
                            style={styles.deleteButton}
                            onClick={() => handleDelete(exame.id)}
                          >
                            Cancelar
                          </button>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan="7" style={styles.noData}>Nenhum exame agendado</td>
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

// Função auxiliar para cores de status
const getStatusColor = (status) => {
  switch(status) {
    case 'agendado': return '#FFF3CD';
    case 'realizado': return '#D4EDDA';
    case 'cancelado': return '#F8D7DA';
    default: return '#F8F9FA';
  }
};

// Estilos
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
  statusSelect: {
    padding: '5px',
    borderRadius: '4px',
    border: '1px solid #ddd',
    cursor: 'pointer',
  },
  resultadoInput: {
    width: '100%',
    padding: '8px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '0.9rem',
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

export default Exames;