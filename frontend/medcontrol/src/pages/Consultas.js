import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';

function Consultas({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de consultas
  const [consultas, setConsultas] = useState([
    {
      id: 1,
      paciente: 'João Silva',
      medico: 'Dr. Carlos Andrade',
      especialidade: 'Cardiologia',
      data: '2023-06-15',
      hora: '14:00',
      observacoes: 'Trazer exames recentes'
    },
    {
      id: 2,
      paciente: 'Maria Oliveira',
      medico: 'Dra. Ana Paula',
      especialidade: 'Dermatologia',
      data: '2023-06-20',
      hora: '10:30',
      observacoes: 'Avaliação de manchas na pele'
    }
  ]);

  const [formData, setFormData] = useState({
    paciente: '',
    medico: '',
    especialidade: '',
    data: '',
    hora: '',
    observacoes: ''
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Filtrar consultas conforme termo de busca
  const filteredConsultas = consultas.filter(consulta =>
    consulta.paciente.toLowerCase().includes(searchTerm.toLowerCase()) ||
    consulta.medico.toLowerCase().includes(searchTerm.toLowerCase()) ||
    consulta.especialidade.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Validação simples
    if (!formData.paciente || !formData.medico || !formData.especialidade || !formData.data || !formData.hora) {
      alert('Preencha os campos obrigatórios: Paciente, Médico, Especialidade, Data e Hora');
      return;
    }

    if (editingId) {
      // Atualizar consulta existente
      setConsultas(consultas.map(consulta => 
        consulta.id === editingId ? { ...formData, id: editingId } : consulta
      ));
    } else {
      // Criar nova consulta
      const novaConsulta = {
        ...formData,
        id: consultas.length > 0 ? Math.max(...consultas.map(c => c.id)) + 1 : 1
      };
      setConsultas([...consultas, novaConsulta]);
    }
    
    // Resetar formulário
    setFormData({
      paciente: '',
      medico: '',
      especialidade: '',
      data: '',
      hora: '',
      observacoes: ''
    });
    setEditingId(null);
  };

  const handleEdit = (consulta) => {
    setFormData({
      paciente: consulta.paciente,
      medico: consulta.medico,
      especialidade: consulta.especialidade,
      data: consulta.data,
      hora: consulta.hora,
      observacoes: consulta.observacoes
    });
    setEditingId(consulta.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir esta consulta?')) {
      setConsultas(consultas.filter(consulta => consulta.id !== id));
    }
  };

  // Funções auxiliares para formatação
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  // Função para verificar se a consulta está no passado
  const isPastConsulta = (dataConsulta) => {
    const hoje = new Date();
    const dataCons = new Date(dataConsulta);
    return dataCons < hoje;
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Agendamento de Consultas</h1>
          <button 
            style={styles.backButton}
            onClick={() => navigate('/dashboard')}
          >
            Voltar ao Dashboard
          </button>
        </div>

        {/* Barra de busca */}
        <div style={styles.searchContainer}>
          <input
            type="text"
            placeholder="Buscar consulta por paciente, médico ou especialidade..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>

        {/* Formulário de cadastro */}
        <form style={styles.form} onSubmit={handleSubmit}>
          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Paciente:</label>
              <input
                type="text"
                name="paciente"
                value={formData.paciente}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Médico:</label>
              <input
                type="text"
                name="medico"
                value={formData.medico}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Especialidade:</label>
              <input
                type="text"
                name="especialidade"
                value={formData.especialidade}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Data:</label>
              <input
                type="date"
                name="data"
                value={formData.data}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Hora:</label>
              <input
                type="time"
                name="hora"
                value={formData.hora}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
          </div>
          
          <div style={styles.formGroup}>
            <label style={styles.label}>Observações:</label>
            <textarea
              name="observacoes"
              value={formData.observacoes}
              onChange={handleInputChange}
              style={{ ...styles.input, minHeight: '80px' }}
            />
          </div>
          
          <div style={styles.formActions}>
            <button type="submit" style={styles.submitButton}>
              {editingId ? 'Atualizar' : 'Agendar'}
            </button>
            {editingId && (
              <button 
                type="button"
                style={styles.cancelButton}
                onClick={() => {
                  setFormData({
                    paciente: '',
                    medico: '',
                    especialidade: '',
                    data: '',
                    hora: '',
                    observacoes: ''
                  });
                  setEditingId(null);
                }}
              >
                Cancelar Edição
              </button>
            )}
          </div>
        </form>

        {/* Lista dinâmica de consultas */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Consultas Agendadas</h2>
          
          {filteredConsultas.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Paciente</th>
                    <th>Médico</th>
                    <th>Especialidade</th>
                    <th>Data/Hora</th>
                    <th>Observações</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredConsultas.map((consulta) => (
                    <tr 
                      key={consulta.id} 
                      style={isPastConsulta(consulta.data) ? styles.pastConsulta : styles.futureConsulta}
                    >
                      <td>{consulta.paciente}</td>
                      <td>{consulta.medico}</td>
                      <td>{consulta.especialidade}</td>
                      <td>{formatDate(consulta.data)} às {consulta.hora}</td>
                      <td>{consulta.observacoes || '-'}</td>
                      <td>
                        {isPastConsulta(consulta.data) ? 'Realizada' : 'Agendada'}
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
                          Excluir
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p style={styles.noResults}>
              {searchTerm ? 'Nenhuma consulta encontrada' : 'Nenhuma consulta agendada'}
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

// Estilos (adaptados do exemplo anterior com adições)
const styles = {
  main: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '2rem',
  },
  headerContainer: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1.5rem',
    flexWrap: 'wrap',
    gap: '1rem',
  },
  title: {
    fontSize: '2rem',
    color: '#282c34',
    margin: 0,
  },
  backButton: {
    backgroundColor: '#6c757d',
    color: 'white',
    border: 'none',
    padding: '8px 15px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.9rem',
    '&:hover': {
      backgroundColor: '#5a6268',
    },
  },
  searchContainer: {
    marginBottom: '1.5rem',
  },
  searchInput: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
    maxWidth: '500px',
  },
  form: {
    backgroundColor: '#f9f9f9',
    padding: '20px',
    borderRadius: '8px',
    marginBottom: '20px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  formRow: {
    display: 'flex',
    gap: '1rem',
    marginBottom: '15px',
  },
  formGroup: {
    flex: 1,
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
  formActions: {
    display: 'flex',
    gap: '1rem',
    marginTop: '1rem',
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
  cancelButton: {
    backgroundColor: '#6c757d',
    color: 'white',
    border: 'none',
    padding: '10px 15px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem',
    '&:hover': {
      backgroundColor: '#5a6268',
    },
  },
  listContainer: {
    marginTop: '2rem',
  },
  listTitle: {
    fontSize: '1.5rem',
    marginBottom: '1rem',
    color: '#282c34',
  },
  tableWrapper: {
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
  pastConsulta: {
    backgroundColor: 'rgba(211, 211, 211, 0.3)',
    color: '#666',
  },
  futureConsulta: {
    backgroundColor: 'rgba(144, 238, 144, 0.3)',
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
  noResults: {
    textAlign: 'center',
    padding: '20px',
    color: '#777',
    backgroundColor: '#f9f9f9',
    borderRadius: '4px',
  },
};

export default Consultas;