import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';

function MedicamentosCadastrados({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de medicamentos
  const [medicamentos, setMedicamentos] = useState([
    {
      id: 1,
      nome: 'Paracetamol',
      principioAtivo: 'Paracetamol',
      laboratorio: 'EMS',
      dosagem: '750mg',
      formaFarmaceutica: 'Comprimido',
      codigoBarras: '7896016801586',
      quantidade: 50,
      preco: 12.90
    },
    {
      id: 2,
      nome: 'Ibuprofeno',
      principioAtivo: 'Ibuprofeno',
      laboratorio: 'Eurofarma',
      dosagem: '400mg',
      formaFarmaceutica: 'Cápsula',
      codigoBarras: '7896181919935',
      quantidade: 30,
      preco: 18.50
    }
  ]);

  const [formData, setFormData] = useState({
    nome: '',
    principioAtivo: '',
    laboratorio: '',
    dosagem: '',
    formaFarmaceutica: '',
    codigoBarras: '',
    quantidade: '',
    preco: ''
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Filtrar medicamentos conforme termo de busca
  const filteredMedicamentos = medicamentos.filter(medicamento =>
    medicamento.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    medicamento.principioAtivo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    medicamento.codigoBarras.includes(searchTerm)
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
    if (!formData.nome || !formData.principioAtivo || !formData.dosagem || 
        !formData.formaFarmaceutica || !formData.quantidade) {
      alert('Preencha os campos obrigatórios: Nome, Princípio Ativo, Dosagem, Forma Farmacêutica e Quantidade');
      return;
    }

    if (editingId) {
      // Atualizar medicamento existente
      setMedicamentos(medicamentos.map(medicamento => 
        medicamento.id === editingId ? { 
          ...formData, 
          id: editingId,
          quantidade: Number(formData.quantidade),
          preco: formData.preco ? Number(formData.preco) : 0
        } : medicamento
      ));
    } else {
      // Criar novo medicamento
      const novoMedicamento = {
        ...formData,
        id: medicamentos.length > 0 ? Math.max(...medicamentos.map(m => m.id)) + 1 : 1,
        quantidade: Number(formData.quantidade),
        preco: formData.preco ? Number(formData.preco) : 0
      };
      setMedicamentos([...medicamentos, novoMedicamento]);
    }
    
    // Resetar formulário
    setFormData({
      nome: '',
      principioAtivo: '',
      laboratorio: '',
      dosagem: '',
      formaFarmaceutica: '',
      codigoBarras: '',
      quantidade: '',
      preco: ''
    });
    setEditingId(null);
  };

  const handleEdit = (medicamento) => {
    setFormData({
      nome: medicamento.nome,
      principioAtivo: medicamento.principioAtivo,
      laboratorio: medicamento.laboratorio,
      dosagem: medicamento.dosagem,
      formaFarmaceutica: medicamento.formaFarmaceutica,
      codigoBarras: medicamento.codigoBarras,
      quantidade: medicamento.quantidade.toString(),
      preco: medicamento.preco.toString()
    });
    setEditingId(medicamento.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir este medicamento?')) {
      setMedicamentos(medicamentos.filter(medicamento => medicamento.id !== id));
    }
  };

  // Função para formatar preço
  const formatPrice = (price) => {
    return price.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Cadastro de Medicamentos</h1>
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
            placeholder="Buscar medicamento por nome, princípio ativo ou código de barras..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>

        {/* Formulário de cadastro */}
        <form style={styles.form} onSubmit={handleSubmit}>
          <div style={styles.formRow}>
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
          </div>

          <div style={styles.formRow}>
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
              <label style={styles.label}>Dosagem:</label>
              <input
                type="text"
                name="dosagem"
                value={formData.dosagem}
                onChange={handleInputChange}
                style={styles.input}
                required
                placeholder="Ex: 500mg"
              />
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Forma Farmacêutica:</label>
              <select
                name="formaFarmaceutica"
                value={formData.formaFarmaceutica}
                onChange={handleInputChange}
                style={styles.input}
                required
              >
                <option value="">Selecione...</option>
                <option value="Comprimido">Comprimido</option>
                <option value="Cápsula">Cápsula</option>
                <option value="Xarope">Xarope</option>
                <option value="Pomada">Pomada</option>
                <option value="Injetável">Injetável</option>
                <option value="Supositório">Supositório</option>
                <option value="Gotas">Gotas</option>
                <option value="Spray">Spray</option>
                <option value="Outra">Outra</option>
              </select>
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Código de Barras:</label>
              <input
                type="text"
                name="codigoBarras"
                value={formData.codigoBarras}
                onChange={handleInputChange}
                style={styles.input}
                placeholder="GTIN/EAN-13"
              />
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.formGroup}>
              <label style={styles.label}>Quantidade em Estoque:</label>
              <input
                type="number"
                name="quantidade"
                value={formData.quantidade}
                onChange={handleInputChange}
                style={styles.input}
                required
                min="0"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Preço Unitário (R$):</label>
              <input
                type="number"
                name="preco"
                value={formData.preco}
                onChange={handleInputChange}
                style={styles.input}
                min="0"
                step="0.01"
                placeholder="Opcional"
              />
            </div>
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
                    principioAtivo: '',
                    laboratorio: '',
                    dosagem: '',
                    formaFarmaceutica: '',
                    codigoBarras: '',
                    quantidade: '',
                    preco: ''
                  });
                  setEditingId(null);
                }}
              >
                Cancelar Edição
              </button>
            )}
          </div>
        </form>

        {/* Lista dinâmica de medicamentos */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Medicamentos Cadastrados</h2>
          
          {filteredMedicamentos.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>Princípio Ativo</th>
                    <th>Dosagem</th>
                    <th>Forma</th>
                    <th>Estoque</th>
                    <th>Preço</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredMedicamentos.map((medicamento) => (
                    <tr key={medicamento.id}>
                      <td>{medicamento.nome}</td>
                      <td>{medicamento.principioAtivo}</td>
                      <td>{medicamento.dosagem}</td>
                      <td>{medicamento.formaFarmaceutica}</td>
                      <td style={medicamento.quantidade <= 10 ? styles.lowStock : null}>
                        {medicamento.quantidade}
                      </td>
                      <td>{medicamento.preco ? formatPrice(medicamento.preco) : '-'}</td>
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
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p style={styles.noResults}>
              {searchTerm ? 'Nenhum medicamento encontrado' : 'Nenhum medicamento cadastrado'}
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

// Estilos (adaptados do exemplo Pacientes.js com adições)
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
  lowStock: {
    color: '#d32f2f',
    fontWeight: 'bold',
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

export default MedicamentosCadastrados;