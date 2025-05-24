import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';

function Lembretes({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de lembretes
  const [lembretes, setLembretes] = useState([
    {
      id: 1,
      titulo: 'Consulta médica',
      descricao: 'Consulta com cardiologista às 14h',
      data: '2023-06-15',
      prioridade: 'alta'
    },
    {
      id: 2,
      titulo: 'Pagamento de conta',
      descricao: 'Pagar conta de luz até o dia 20',
      data: '2023-06-20',
      prioridade: 'media'
    }
  ]);

  const [formData, setFormData] = useState({
    titulo: '',
    descricao: '',
    data: '',
    prioridade: 'baixa'
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Filtrar lembretes conforme termo de busca
  const filteredLembretes = lembretes.filter(lembrete =>
    lembrete.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    lembrete.descricao.toLowerCase().includes(searchTerm.toLowerCase())
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
    if (!formData.titulo || !formData.data) {
      alert('Preencha os campos obrigatórios: Título e Data');
      return;
    }

    if (editingId) {
      // Atualizar lembrete existente
      setLembretes(lembretes.map(lembrete => 
        lembrete.id === editingId ? { ...formData, id: editingId } : lembrete
      ));
    } else {
      // Criar novo lembrete
      const novoLembrete = {
        ...formData,
        id: lembretes.length > 0 ? Math.max(...lembretes.map(l => l.id)) + 1 : 1
      };
      setLembretes([...lembretes, novoLembrete]);
    }
    
    // Resetar formulário
    setFormData({
      titulo: '',
      descricao: '',
      data: '',
      prioridade: 'baixa'
    });
    setEditingId(null);
  };

  const handleEdit = (lembrete) => {
    setFormData({
      titulo: lembrete.titulo,
      descricao: lembrete.descricao,
      data: lembrete.data,
      prioridade: lembrete.prioridade
    });
    setEditingId(lembrete.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este lembrete?')) {
      setLembretes(lembretes.filter(lembrete => lembrete.id !== id));
    }
  };

  // Função auxiliar para formatação de data
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  // Função para retornar a classe CSS baseada na prioridade
  const getPriorityClass = (prioridade) => {
    switch (prioridade) {
      case 'alta':
        return styles.highPriority;
      case 'media':
        return styles.mediumPriority;
      default:
        return styles.lowPriority;
    }
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Gerenciamento de Lembretes</h1>
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
            placeholder="Buscar lembrete por título ou descrição..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>

        {/* Formulário de cadastro */}
        <form style={styles.form} onSubmit={handleSubmit}>
          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Título:</label>
              <input
                type="text"
                name="titulo"
                value={formData.titulo}
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

          <div style={styles.formGroup}>
            <label style={styles.label}>Descrição:</label>
            <textarea
              name="descricao"
              value={formData.descricao}
              onChange={handleInputChange}
              style={{ ...styles.input, minHeight: '80px' }}
            />
          </div>
          
          <div style={styles.formGroup}>
            <label style={styles.label}>Prioridade:</label>
            <select
              name="prioridade"
              value={formData.prioridade}
              onChange={handleInputChange}
              style={styles.input}
            >
              <option value="baixa">Baixa</option>
              <option value="media">Média</option>
              <option value="alta">Alta</option>
            </select>
          </div>
          
          <div style={styles.formActions}>
            <button type="submit" style={styles.submitButton}>
              {editingId ? 'Atualizar' : 'Cadastrar'}
            </button>
            {editingId && (
              <button 
                type="button"
                style={styles.cancelButton}
                onClick={() => {
                  setFormData({
                    titulo: '',
                    descricao: '',
                    data: '',
                    prioridade: 'baixa'
                  });
                  setEditingId(null);
                }}
              >
                Cancelar Edição
              </button>
            )}
          </div>
        </form>

        {/* Lista dinâmica de lembretes */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Lembretes Cadastrados</h2>
          
          {filteredLembretes.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Título</th>
                    <th>Descrição</th>
                    <th>Data</th>
                    <th>Prioridade</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredLembretes.map((lembrete) => (
                    <tr key={lembrete.id} style={getPriorityClass(lembrete.prioridade)}>
                      <td>{lembrete.titulo}</td>
                      <td>{lembrete.descricao || '-'}</td>
                      <td>{formatDate(lembrete.data)}</td>
                      <td>
                        {lembrete.prioridade === 'alta' && 'Alta'}
                        {lembrete.prioridade === 'media' && 'Média'}
                        {lembrete.prioridade === 'baixa' && 'Baixa'}
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
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p style={styles.noResults}>
              {searchTerm ? 'Nenhum lembrete encontrado' : 'Nenhum lembrete cadastrado'}
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
  highPriority: {
    backgroundColor: 'rgba(255, 0, 0, 0.1)',
    '&:hover': {
      backgroundColor: 'rgba(255, 0, 0, 0.2)',
    },
  },
  mediumPriority: {
    backgroundColor: 'rgba(255, 165, 0, 0.1)',
    '&:hover': {
      backgroundColor: 'rgba(255, 165, 0, 0.2)',
    },
  },
  lowPriority: {
    backgroundColor: 'rgba(0, 128, 0, 0.1)',
    '&:hover': {
      backgroundColor: 'rgba(0, 128, 0, 0.2)',
    },
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

export default Lembretes;