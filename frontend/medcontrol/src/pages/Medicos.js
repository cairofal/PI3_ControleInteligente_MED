import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';

function Medicos({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de médicos
  const [medicos, setMedicos] = useState([
    {
      id: 1,
      nome: 'Dr. Carlos Andrade',
      crm: 'SP123456',
      especialidade: 'Cardiologia',
      telefone: '(11) 98765-4321',
      email: 'carlos.andrade@clinica.com',
      endereco: 'Av. Paulista, 1000, São Paulo - SP'
    },
    {
      id: 2,
      nome: 'Dra. Ana Paula Silva',
      crm: 'SP654321',
      especialidade: 'Dermatologia',
      telefone: '(11) 91234-5678',
      email: 'ana.silva@clinica.com',
      endereco: 'R. Oscar Freire, 2000, São Paulo - SP'
    }
  ]);

  const [formData, setFormData] = useState({
    nome: '',
    crm: '',
    especialidade: '',
    telefone: '',
    email: '',
    endereco: ''
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Filtrar médicos conforme termo de busca
  const filteredMedicos = medicos.filter(medico =>
    medico.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    medico.crm.includes(searchTerm) ||
    medico.especialidade.toLowerCase().includes(searchTerm.toLowerCase())
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
    if (!formData.nome || !formData.crm || !formData.especialidade || !formData.telefone) {
      alert('Preencha os campos obrigatórios: Nome, CRM, Especialidade e Telefone');
      return;
    }

    if (editingId) {
      // Atualizar médico existente
      setMedicos(medicos.map(medico => 
        medico.id === editingId ? { ...formData, id: editingId } : medico
      ));
    } else {
      // Criar novo médico
      const novoMedico = {
        ...formData,
        id: medicos.length > 0 ? Math.max(...medicos.map(m => m.id)) + 1 : 1
      };
      setMedicos([...medicos, novoMedico]);
    }
    
    // Resetar formulário
    setFormData({
      nome: '',
      crm: '',
      especialidade: '',
      telefone: '',
      email: '',
      endereco: ''
    });
    setEditingId(null);
  };

  const handleEdit = (medico) => {
    setFormData({
      nome: medico.nome,
      crm: medico.crm,
      especialidade: medico.especialidade,
      telefone: medico.telefone,
      email: medico.email,
      endereco: medico.endereco
    });
    setEditingId(medico.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este médico?')) {
      setMedicos(medicos.filter(medico => medico.id !== id));
    }
  };

  // Função para formatar CRM (ex: SP123456 -> SP-123456)
  const formatCRM = (crm) => {
    if (!crm) return '';
    // Adiciona hífen após 2 caracteres (para CRMs no formato UF123456)
    return crm.length > 2 ? `${crm.slice(0, 2)}-${crm.slice(2)}` : crm;
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Cadastro de Médicos</h1>
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
            placeholder="Buscar médico por nome, CRM ou especialidade..."
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
                placeholder="Dr. Nome Sobrenome"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>CRM:</label>
              <input
                type="text"
                name="crm"
                value={formData.crm}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="UF123456"
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
                placeholder="Ex: Cardiologia"
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
              placeholder="exemplo@clinica.com"
            />
          </div>
          
          <div style={styles.formGroup}>
            <label style={styles.label}>Endereço Profissional:</label>
            <input
              type="text"
              name="endereco"
              value={formData.endereco}
              onChange={handleInputChange}
              style={styles.input}
              placeholder="Rua, número, cidade - UF"
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
                    crm: '',
                    especialidade: '',
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

        {/* Lista dinâmica de médicos */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Médicos Cadastrados</h2>
          
          {filteredMedicos.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>CRM</th>
                    <th>Especialidade</th>
                    <th>Telefone</th>
                    <th>Email</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredMedicos.map((medico) => (
                    <tr key={medico.id}>
                      <td>{medico.nome}</td>
                      <td>{formatCRM(medico.crm)}</td>
                      <td>{medico.especialidade}</td>
                      <td>{medico.telefone}</td>
                      <td>{medico.email || '-'}</td>
                      <td style={styles.actions}>
                        <button 
                          style={styles.editButton}
                          onClick={() => handleEdit(medico)}
                        >
                          Editar
                        </button>
                        <button 
                          style={styles.deleteButton}
                          onClick={() => handleDelete(medico.id)}
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
              {searchTerm ? 'Nenhum médico encontrado' : 'Nenhum médico cadastrado'}
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

// Estilos (os mesmos do exemplo Pacientes.js com pequenos ajustes)
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

export default Medicos;