import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config';
import Header from '../components/Header';

function Lembretes({ isLoggedIn, handleLogout }) {
  const [lembretes, setLembretes] = useState([]);
  const [medicamentos, setMedicamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    medicamentoId: '',
    dataHora: '',
    descricao: '',
    repetir: 'nunca',
    intervaloRepeticao: 1
  });
  const [editingId, setEditingId] = useState(null);
  const [notificacao, setNotificacao] = useState(null);

  useEffect(() => {
    fetchLembretes();
    fetchMedicamentos();
    verificarLembretesAtivos();
    
    // Verificar lembretes a cada minuto
    const intervalo = setInterval(verificarLembretesAtivos, 60000);
    
    return () => clearInterval(intervalo);
  }, []);

  const fetchLembretes = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/lembretes`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setLembretes(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Erro ao buscar lembretes:', error);
      setError('Erro ao carregar lembretes');
      setLoading(false);
    }
  };

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
    }
  };

  const verificarLembretesAtivos = () => {
    const agora = new Date();
    const lembretesAtivos = lembretes.filter(lembrete => {
      const dataHoraLembrete = new Date(lembrete.dataHora);
      return (
        dataHoraLembrete <= agora && 
        (!lembrete.ultimaNotificacao || new Date(lembrete.ultimaNotificacao) < dataHoraLembrete)
      );
    });

    if (lembretesAtivos.length > 0) {
      const primeiroLembrete = lembretesAtivos[0];
      const medicamento = medicamentos.find(m => m.id === primeiroLembrete.medicamentoId);
      
      setNotificacao({
        titulo: `Hora de tomar seu medicamento: ${medicamento?.nome || 'Medicamento'}`,
        mensagem: primeiroLembrete.descricao || `Lembrete de ${medicamento?.nome || 'medicamento'}`,
        dataHora: primeiroLembrete.dataHora
      });

      // Marcar como notificado
      marcarComoNotificado(primeiroLembrete.id);
    }
  };

  const marcarComoNotificado = async (lembreteId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(`${API_BASE_URL}/lembretes/${lembreteId}/notificar`, {}, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      fetchLembretes();
    } catch (error) {
      console.error('Erro ao marcar lembrete como notificado:', error);
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
        // Atualizar lembrete existente
        await axios.put(`${API_BASE_URL}/lembretes/${editingId}`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      } else {
        // Criar novo lembrete
        await axios.post(`${API_BASE_URL}/lembretes`, formData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      }
      
      // Resetar formulário e recarregar lista
      setShowForm(false);
      setFormData({
        medicamentoId: '',
        dataHora: '',
        descricao: '',
        repetir: 'nunca',
        intervaloRepeticao: 1
      });
      setEditingId(null);
      fetchLembretes();
    } catch (error) {
      console.error('Erro ao salvar lembrete:', error);
      setError('Erro ao salvar lembrete');
    }
  };

  const handleEdit = (lembrete) => {
    setFormData({
      medicamentoId: lembrete.medicamentoId,
      dataHora: lembrete.dataHora.slice(0, 16), // Ajuste para input datetime-local
      descricao: lembrete.descricao,
      repetir: lembrete.repetir,
      intervaloRepeticao: lembrete.intervaloRepeticao || 1
    });
    setEditingId(lembrete.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este lembrete?')) {
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`${API_BASE_URL}/lembretes/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        fetchLembretes();
      } catch (error) {
        console.error('Erro ao excluir lembrete:', error);
        setError('Erro ao excluir lembrete');
      }
    }
  };

  const fecharNotificacao = () => {
    setNotificacao(null);
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <h1 style={styles.title}>Lembretes de Medicamentos</h1>
        
        {notificacao && (
          <div style={styles.notificacao}>
            <div style={styles.notificacaoContent}>
              <h3 style={styles.notificacaoTitulo}>{notificacao.titulo}</h3>
              <p style={styles.notificacaoMensagem}>{notificacao.mensagem}</p>
              <p style={styles.notificacaoHorario}>
                {new Date(notificacao.dataHora).toLocaleString()}
              </p>
              <button 
                style={styles.notificacaoBotao}
                onClick={fecharNotificacao}
              >
                OK
              </button>
            </div>
          </div>
        )}

        <button 
          style={styles.addButton}
          onClick={() => {
            setShowForm(!showForm);
            if (showForm) {
              setEditingId(null);
              setFormData({
                medicamentoId: '',
                dataHora: '',
                descricao: '',
                repetir: 'nunca',
                intervaloRepeticao: 1
              });
            }
          }}
        >
          {showForm ? 'Cancelar' : '+ Adicionar Lembrete'}
        </button>

        {showForm && (
          <form style={styles.form} onSubmit={handleSubmit}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Medicamento:</label>
              <select
                name="medicamentoId"
                value={formData.medicamentoId}
                onChange={handleInputChange}
                style={styles.input}
                required
              >
                <option value="">Selecione um medicamento</option>
                {medicamentos.map((medicamento) => (
                  <option key={medicamento.id} value={medicamento.id}>
                    {medicamento.nome} ({medicamento.dosagem})
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
              <label style={styles.label}>Descrição:</label>
              <textarea
                name="descricao"
                value={formData.descricao}
                onChange={handleInputChange}
                style={{...styles.input, minHeight: '80px'}}
                placeholder="Ex: Tomar 1 comprimido com água"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Repetir:</label>
              <select
                name="repetir"
                value={formData.repetir}
                onChange={handleInputChange}
                style={styles.input}
              >
                <option value="nunca">Não repetir</option>
                <option value="diariamente">Diariamente</option>
                <option value="semanalmente">Semanalmente</option>
                <option value="mensalmente">Mensalmente</option>
              </select>
            </div>
            
            {formData.repetir !== 'nunca' && (
              <div style={styles.formGroup}>
                <label style={styles.label}>Intervalo (dias/semanas/meses):</label>
                <input
                  type="number"
                  name="intervaloRepeticao"
                  value={formData.intervaloRepeticao}
                  onChange={handleInputChange}
                  style={styles.input}
                  min="1"
                />
              </div>
            )}
            
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
                  <th>Medicamento</th>
                  <th>Data/Hora</th>
                  <th>Descrição</th>
                  <th>Repetição</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {lembretes.length > 0 ? (
                  lembretes.map((lembrete) => {
                    const medicamento = medicamentos.find(m => m.id === lembrete.medicamentoId);
                    return (
                      <tr key={lembrete.id}>
                        <td>{medicamento?.nome || 'Medicamento não encontrado'}</td>
                        <td>{new Date(lembrete.dataHora).toLocaleString()}</td>
                        <td>{lembrete.descricao || '-'}</td>
                        <td>
                          {lembrete.repetir === 'nunca' ? 'Não repete' : 
                           `${lembrete.repetir} (a cada ${lembrete.intervaloRepeticao} ${lembrete.repetir === 'diariamente' ? 'dia(s)' : 
                             lembrete.repetir === 'semanalmente' ? 'semana(s)' : 'mês(es)'})`}
                        </td>
                        <td style={styles.actions}>
                          <button 
                            style={styles.editButton}
                            onClick={() => handleEdit(lembrete)}
                          >
                            Editar
                          </button>
                          <button 
                            style={styles.deleteButton}
                            onClick={() => handleDelete(lembrete.id)}
                          >
                            Excluir
                          </button>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan="5" style={styles.noData}>Nenhum lembrete cadastrado</td>
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

// Estilos
const styles = {
  main: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '2rem',
    position: 'relative',
  },
  title: {
    fontSize: '2rem',
    marginBottom: '1.5rem',
    color: '#282c34',
  },
  notificacao: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000,
  },
  notificacaoContent: {
    backgroundColor: 'white',
    padding: '2rem',
    borderRadius: '8px',
    maxWidth: '500px',
    width: '90%',
    boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
    textAlign: 'center',
  },
  notificacaoTitulo: {
    color: '#d32f2f',
    fontSize: '1.5rem',
    marginBottom: '1rem',
  },
  notificacaoMensagem: {
    fontSize: '1.2rem',
    marginBottom: '1rem',
  },
  notificacaoHorario: {
    color: '#666',
    marginBottom: '1.5rem',
  },
  notificacaoBotao: {
    backgroundColor: '#2196F3',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem',
    '&:hover': {
      backgroundColor: '#0b7dda',
    },
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

export default Lembretes;