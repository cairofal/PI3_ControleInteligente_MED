import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config';
import Header from '../components/Header';

function Consultas({ isLoggedIn, handleLogout }) {
  const [consultas, setConsultas] = useState([]);
  const [medicos, setMedicos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    medicoId: '',
    dataHora: '',
    descricao: '',
    local: '',
    especialidade: '',
    status: 'agendada'
  });
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    fetchConsultas();
    fetchMedicos();
  }, []);

  const fetchConsultas = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/consultas`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setConsultas(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Erro ao buscar consultas:', error);
      setError('Erro ao carregar consultas');
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
        // Atualizar consulta existente
        await axios.put(`${API_BASE_URL}/consultas/${editingId}`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      } else {
        // Criar nova consulta
        await axios.post(`${API_BASE_URL}/consultas`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      }
      
      // Resetar formulário e recarregar lista
      setShowForm(false);
      setFormData({
        medicoId: '',
        dataHora: '',
        descricao: '',
        local: '',
        especialidade: '',
        status: 'agendada'
      });
      setEditingId(null);
      fetchConsultas();
    } catch (error) {
      console.error('Erro ao salvar consulta:', error);
      setError('Erro ao salvar consulta');
    }
  };

  const handleEdit = (consulta) => {
    setFormData({
      medicoId: consulta.medicoId,
      dataHora: consulta.dataHora.slice(0, 16), // Ajuste para input datetime-local
      descricao: consulta.descricao,
      local: consulta.local,
      especialidade: consulta.especialidade,
      status: consulta.status
    });
    setEditingId(consulta.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja cancelar esta consulta?')) {
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`${API_BASE_URL}/consultas/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        fetchConsultas();
      } catch (error) {
        console.error('Erro ao cancelar consulta:', error);
        setError('Erro ao cancelar consulta');
      }
    }
  };

  const handleStatusChange = async (id, novoStatus) => {
    try {
      const token = localStorage.getItem('token');
      await axios.patch(`${API_BASE_URL}/consultas/${id}/status`, 
        { status: novoStatus },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      fetchConsultas();
    } catch (error) {
      console.error('Erro ao atualizar status:', error);
      setError('Erro ao atualizar status da consulta');
    }
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <h1 style={styles.title}>Agenda de Consultas</h1>
        
        <button 
          style={styles.addButton}
          onClick={() => {
            setShowForm(!showForm);
            if (showForm) {
              setEditingId(null);
              setFormData({
                medicoId: '',
                dataHora: '',
                descricao: '',
                local: '',
                especialidade: '',
                status: 'agendada'
              });
            }
          }}
        >
          {showForm ? 'Cancelar' : '+ Agendar Consulta'}
        </button>

        {showForm && (
          <form style={styles.form} onSubmit={handleSubmit}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Médico:</label>
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
              <label style={styles.label}>Especialidade:</label>
              <input
                type="text"
                name="especialidade"
                value={formData.especialidade}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="Ex: Cardiologia, Dermatologia"
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
                placeholder="Ex: Hospital X, Sala 203"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Descrição:</label>
              <textarea
                name="descricao"
                value={formData.descricao}
                onChange={handleInputChange}
                style={{...styles.input, minHeight: '80px'}}
                placeholder="Motivo da consulta, sintomas, etc."
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
                <option value="agendada">Agendada</option>
                <option value="confirmada">Confirmada</option>
                <option value="realizada">Realizada</option>
                <option value="cancelada">Cancelada</option>
              </select>
            </div>
            
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
                  <th>Médico</th>
                  <th>Data/Hora</th>
                  <th>Especialidade</th>
                  <th>Local</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {consultas.length > 0 ? (
                  consultas.map((consulta) => {
                    const medico = medicos.find(m => m.id === consulta.medicoId);
                    return (
                      <tr key={consulta.id}>
                        <td>Dr. {medico?.nome || 'Médico não encontrado'}</td>
                        <td>{new Date(consulta.dataHora).toLocaleString()}</td>
                        <td>{consulta.especialidade}</td>
                        <td>{consulta.local}</td>
                        <td>
                          <select
                            value={consulta.status}
                            onChange={(e) => handleStatusChange(consulta.id, e.target.value)}
                            style={{
                              ...styles.statusSelect,
                              backgroundColor: getStatusColor(consulta.status)
                            }}
                          >
                            <option value="agendada">Agendada</option>
                            <option value="confirmada">Confirmada</option>
                            <option value="realizada">Realizada</option>
                            <option value="cancelada">Cancelada</option>
                          </select>
                        </td>
                        <td style={styles.actions}>
                          <button 
                            style={styles.editButton}
                            onClick={() => handleEdit(consulta)}
                          >
                            Editar
                          </button>
                          <button 
                            style={styles.deleteButton}
                            onClick={() => handleDelete(consulta.id)}
                          >
                            Cancelar
                          </button>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan="6" style={styles.noData}>Nenhuma consulta agendada</td>
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
    case 'agendada': return '#FFF3CD';
    case 'confirmada': return '#D1ECF1';
    case 'realizada': return '#D4EDDA';
    case 'cancelada': return '#F8D7DA';
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

export default Consultas;