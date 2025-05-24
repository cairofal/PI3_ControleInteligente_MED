import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';

function Exames({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de exames
  const [exames, setExames] = useState([
    {
      id: 1,
      tipo: 'Hemograma completo',
      paciente: 'João Silva',
      data: '2023-06-15',
      hora: '08:00',
      local: 'Laboratório ABC',
      medicoSolicitante: 'Dr. Carlos Andrade',
      observacoes: 'Jejum de 8 horas'
    },
    {
      id: 2,
      tipo: 'Ressonância Magnética',
      paciente: 'Maria Oliveira',
      data: '2023-06-20',
      hora: '14:30',
      local: 'Clínica de Imagens XYZ',
      medicoSolicitante: 'Dra. Ana Paula',
      observacoes: 'Trazer exames anteriores'
    }
  ]);

  const [formData, setFormData] = useState({
    tipo: '',
    paciente: '',
    data: '',
    hora: '',
    local: '',
    medicoSolicitante: '',
    observacoes: ''
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Filtrar exames conforme termo de busca
  const filteredExames = exames.filter(exame =>
    exame.tipo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    exame.paciente.toLowerCase().includes(searchTerm.toLowerCase()) ||
    exame.local.toLowerCase().includes(searchTerm.toLowerCase())
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
    if (!formData.tipo || !formData.paciente || !formData.data || !formData.hora || !formData.local) {
      alert('Preencha os campos obrigatórios: Tipo, Paciente, Data, Hora e Local');
      return;
    }

    if (editingId) {
      // Atualizar exame existente
      setExames(exames.map(exame => 
        exame.id === editingId ? { ...formData, id: editingId } : exame
      ));
    } else {
      // Criar novo exame
      const novoExame = {
        ...formData,
        id: exames.length > 0 ? Math.max(...exames.map(e => e.id)) + 1 : 1
      };
      setExames([...exames, novoExame]);
    }
    
    // Resetar formulário
    setFormData({
      tipo: '',
      paciente: '',
      data: '',
      hora: '',
      local: '',
      medicoSolicitante: '',
      observacoes: ''
    });
    setEditingId(null);
  };

  const handleEdit = (exame) => {
    setFormData({
      tipo: exame.tipo,
      paciente: exame.paciente,
      data: exame.data,
      hora: exame.hora,
      local: exame.local,
      medicoSolicitante: exame.medicoSolicitante,
      observacoes: exame.observacoes
    });
    setEditingId(exame.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este exame?')) {
      setExames(exames.filter(exame => exame.id !== id));
    }
  };

  // Funções auxiliares para formatação
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  // Função para verificar se o exame está no passado
  const isPastExame = (dataExame) => {
    const hoje = new Date();
    const dataEx = new Date(dataExame);
    return dataEx < hoje;
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Agendamento de Exames</h1>
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
            placeholder="Buscar exame por tipo, paciente ou local..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>

        {/* Formulário de cadastro */}
        <form style={styles.form} onSubmit={handleSubmit}>
          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Tipo de Exame:</label>
              <input
                type="text"
                name="tipo"
                value={formData.tipo}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
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
          </div>

          <div style={styles.formRow}>
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

          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Local:</label>
              <input
                type="text"
                name="local"
                value={formData.local}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Médico Solicitante:</label>
              <input
                type="text"
                name="medicoSolicitante"
                value={formData.medicoSolicitante}
                onChange={handleInputChange}
                style={styles.input}
              />
            </div>
          </div>
          
          <div style={styles.formGroup}>
            <label style={styles.label}>Observações/Preparação:</label>
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
                    tipo: '',
                    paciente: '',
                    data: '',
                    hora: '',
                    local: '',
                    medicoSolicitante: '',
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

        {/* Lista dinâmica de exames */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Exames Agendados</h2>
          
          {filteredExames.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Tipo</th>
                    <th>Paciente</th>
                    <th>Data/Hora</th>
                    <th>Local</th>
                    <th>Médico</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredExames.map((exame) => (
                    <tr 
                      key={exame.id} 
                      style={isPastExame(exame.data) ? styles.pastExame : styles.futureExame}
                    >
                      <td>{exame.tipo}</td>
                      <td>{exame.paciente}</td>
                      <td>{formatDate(exame.data)} às {exame.hora}</td>
                      <td>{exame.local}</td>
                      <td>{exame.medicoSolicitante || '-'}</td>
                      <td>
                        {isPastExame(exame.data) ? 'Realizado' : 'Agendado'}
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
              {searchTerm ? 'Nenhum exame encontrado' : 'Nenhum exame agendado'}
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
  pastExame: {
    backgroundColor: 'rgba(211, 211, 211, 0.3)',
    color: '#666',
  },
  futureExame: {
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

export default Exames;