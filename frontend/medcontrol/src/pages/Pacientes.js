import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';

function Pacientes({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de pacientes
  const [pacientes, setPacientes] = useState([
    {
      id: 1,
      nome: 'João Silva',
      cpf: '12345678901',
      dataNascimento: '1980-05-15',
      telefone: '11987654321',
      email: 'joao@exemplo.com',
      endereco: 'Rua das Flores, 123'
    },
    {
      id: 2,
      nome: 'Maria Oliveira',
      cpf: '98765432109',
      dataNascimento: '1990-08-20',
      telefone: '21912345678',
      email: 'maria@exemplo.com',
      endereco: 'Av. Paulista, 1000'
    }
  ]);

  const [formData, setFormData] = useState({
    nome: '',
    cpf: '',
    dataNascimento: '',
    telefone: '',
    email: '',
    endereco: ''
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Filtrar pacientes conforme termo de busca
  const filteredPacientes = pacientes.filter(paciente =>
    paciente.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    paciente.cpf.includes(searchTerm)
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
    if (!formData.nome || !formData.cpf || !formData.dataNascimento || !formData.telefone) {
      alert('Preencha os campos obrigatórios: Nome, CPF, Data de Nascimento e Telefone');
      return;
    }

    if (editingId) {
      // Atualizar paciente existente
      setPacientes(pacientes.map(paciente => 
        paciente.id === editingId ? { ...formData, id: editingId } : paciente
      ));
    } else {
      // Criar novo paciente
      const novoPaciente = {
        ...formData,
        id: pacientes.length > 0 ? Math.max(...pacientes.map(p => p.id)) + 1 : 1
      };
      setPacientes([...pacientes, novoPaciente]);
    }
    
    // Resetar formulário
    setFormData({
      nome: '',
      cpf: '',
      dataNascimento: '',
      telefone: '',
      email: '',
      endereco: ''
    });
    setEditingId(null);
  };

  const handleEdit = (paciente) => {
    setFormData({
      nome: paciente.nome,
      cpf: paciente.cpf,
      dataNascimento: paciente.dataNascimento,
      telefone: paciente.telefone,
      email: paciente.email,
      endereco: paciente.endereco
    });
    setEditingId(paciente.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este paciente?')) {
      setPacientes(pacientes.filter(paciente => paciente.id !== id));
    }
  };

  // Funções auxiliares para formatação
  const formatCPF = (cpf) => {
    if (!cpf) return '';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  const formatPhone = (phone) => {
    if (!phone) return '';
    // Formatação para (00) 00000-0000
    const cleaned = phone.replace(/\D/g, '');
    const match = cleaned.match(/^(\d{2})(\d{5})(\d{4})$/);
    if (match) {
      return `(${match[1]}) ${match[2]}-${match[3]}`;
    }
    return phone;
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Cadastro de Pacientes</h1>
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
            placeholder="Buscar paciente por nome ou CPF..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>

        {/* Formulário de cadastro */}
        <form style={styles.form} onSubmit={handleSubmit}>
          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Nome Completo:</label>
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
              <label style={styles.label}>CPF:</label>
              <input
                type="text"
                name="cpf"
                value={formData.cpf}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="000.000.000-00"
              />
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Data de Nascimento:</label>
              <input
                type="date"
                name="dataNascimento"
                value={formData.dataNascimento}
                onChange={handleInputChange}
                style={styles.input}
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Telefone:</label>
              <input
                type="tel"
                name="telefone"
                value={formData.telefone}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="(00) 00000-0000"
              />
            </div>
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Email:</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              style={styles.input}
            />
          </div>
          
          <div style={styles.formGroup}>
            <label style={styles.label}>Endereço:</label>
            <input
              type="text"
              name="endereco"
              value={formData.endereco}
              onChange={handleInputChange}
              style={styles.input}
            />
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
                    nome: '',
                    cpf: '',
                    dataNascimento: '',
                    telefone: '',
                    email: '',
                    endereco: ''
                  });
                  setEditingId(null);
                }}
              >
                Cancelar Edição
              </button>
            )}
          </div>
        </form>

        {/* Lista dinâmica de pacientes */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Pacientes Cadastrados</h2>
          
          {filteredPacientes.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>CPF</th>
                    <th>Nascimento</th>
                    <th>Telefone</th>
                    <th>Email</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredPacientes.map((paciente) => (
                    <tr key={paciente.id}>
                      <td>{paciente.nome}</td>
                      <td>{formatCPF(paciente.cpf)}</td>
                      <td>{formatDate(paciente.dataNascimento)}</td>
                      <td>{formatPhone(paciente.telefone)}</td>
                      <td>{paciente.email || '-'}</td>
                      <td style={styles.actions}>
                        <button 
                          style={styles.editButton}
                          onClick={() => handleEdit(paciente)}
                        >
                          Editar
                        </button>
                        <button 
                          style={styles.deleteButton}
                          onClick={() => handleDelete(paciente.id)}
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
              {searchTerm ? 'Nenhum paciente encontrado' : 'Nenhum paciente cadastrado'}
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

// Estilos (os mesmos do exemplo anterior)
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
  noResults: {
    textAlign: 'center',
    padding: '20px',
    color: '#777',
    backgroundColor: '#f9f9f9',
    borderRadius: '4px',
  },
};

export default Pacientes;