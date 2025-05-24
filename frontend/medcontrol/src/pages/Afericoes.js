import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function Afericoes({ isLoggedIn, handleLogout }) {
  const navigate = useNavigate();
  
  // Estado para simular o "banco de dados" de aferições
  const [afericoes, setAfericoes] = useState([
    {
      id: 1,
      data: '2023-06-01',
      hora: '08:00',
      glicose: 95,
      pressaoSistolica: 120,
      pressaoDiastolica: 80,
      observacoes: 'Em jejum'
    },
    {
      id: 2,
      data: '2023-06-02',
      hora: '18:30',
      glicose: 110,
      pressaoSistolica: 130,
      pressaoDiastolica: 85,
      observacoes: 'Após jantar'
    },
    {
      id: 3,
      data: '2023-06-03',
      hora: '08:15',
      glicose: 90,
      pressaoSistolica: 125,
      pressaoDiastolica: 82,
      observacoes: 'Em jejum'
    }
  ]);

  const [formData, setFormData] = useState({
    data: '',
    hora: '',
    glicose: '',
    pressaoSistolica: '',
    pressaoDiastolica: '',
    observacoes: ''
  });
  
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [showChart, setShowChart] = useState(false);

  // Filtrar aferições conforme termo de busca
  const filteredAfericoes = afericoes.filter(afericao =>
    afericao.data.includes(searchTerm) ||
    afericao.observacoes.toLowerCase().includes(searchTerm.toLowerCase())
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
    if (!formData.data || !formData.hora || !formData.glicose || 
        !formData.pressaoSistolica || !formData.pressaoDiastolica) {
      alert('Preencha os campos obrigatórios: Data, Hora, Glicose e Pressão Arterial');
      return;
    }

    if (editingId) {
      // Atualizar aferição existente
      setAfericoes(afericoes.map(afericao => 
        afericao.id === editingId ? { 
          ...formData, 
          id: editingId,
          glicose: Number(formData.glicose),
          pressaoSistolica: Number(formData.pressaoSistolica),
          pressaoDiastolica: Number(formData.pressaoDiastolica)
        } : afericao
      ));
    } else {
      // Criar nova aferição
      const novaAfericao = {
        ...formData,
        id: afericoes.length > 0 ? Math.max(...afericoes.map(a => a.id)) + 1 : 1,
        glicose: Number(formData.glicose),
        pressaoSistolica: Number(formData.pressaoSistolica),
        pressaoDiastolica: Number(formData.pressaoDiastolica)
      };
      setAfericoes([...afericoes, novaAfericao]);
    }
    
    // Resetar formulário
    setFormData({
      data: '',
      hora: '',
      glicose: '',
      pressaoSistolica: '',
      pressaoDiastolica: '',
      observacoes: ''
    });
    setEditingId(null);
  };

  const handleEdit = (afericao) => {
    setFormData({
      data: afericao.data,
      hora: afericao.hora,
      glicose: afericao.glicose.toString(),
      pressaoSistolica: afericao.pressaoSistolica.toString(),
      pressaoDiastolica: afericao.pressaoDiastolica.toString(),
      observacoes: afericao.observacoes
    });
    setEditingId(afericao.id);
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja excluir esta aferição?')) {
      setAfericoes(afericoes.filter(afericao => afericao.id !== id));
    }
  };

  // Funções auxiliares para formatação
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  // Preparar dados para o gráfico
  const prepareChartData = () => {
    return afericoes
      .sort((a, b) => new Date(a.data) - new Date(b.data))
      .map(afericao => ({
        data: formatDate(afericao.data),
        glicose: afericao.glicose,
        pressaoSistolica: afericao.pressaoSistolica,
        pressaoDiastolica: afericao.pressaoDiastolica
      }));
  };

  // Função para avaliar o nível de glicose
  const getGlicoseStatus = (glicose) => {
    if (glicose < 70) return 'Hipoglicemia';
    if (glicose >= 70 && glicose <= 99) return 'Normal';
    if (glicose >= 100 && glicose <= 125) return 'Pré-diabetes';
    return 'Diabetes';
  };

  // Função para avaliar a pressão arterial
  const getPressaoStatus = (sistolica, diastolica) => {
    if (sistolica < 120 && diastolica < 80) return 'Normal';
    if (sistolica >= 120 && sistolica <= 129 && diastolica < 80) return 'Elevada';
    if ((sistolica >= 130 && sistolica <= 139) || (diastolica >= 80 && diastolica <= 89)) return 'Hipertensão Estágio 1';
    if (sistolica >= 140 || diastolica >= 90) return 'Hipertensão Estágio 2';
    return 'Crise Hipertensiva';
  };

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
      <main style={styles.main}>
        <div style={styles.headerContainer}>
          <h1 style={styles.title}>Controle de Aferições</h1>
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
            placeholder="Buscar aferição por data ou observação..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>

        {/* Formulário de cadastro */}
        <form style={styles.form} onSubmit={handleSubmit}>
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
              <label style={styles.label}>Glicose (mg/dL):</label>
              <input
                type="number"
                name="glicose"
                value={formData.glicose}
                onChange={handleInputChange}
                style={styles.input}
                required
                min="0"
                step="1"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label}>Pressão Arterial:</label>
              <div style={{ display: 'flex', gap: '8px' }}>
                <input
                  type="number"
                  name="pressaoSistolica"
                  value={formData.pressaoSistolica}
                  onChange={handleInputChange}
                  style={{ ...styles.input, flex: 1 }}
                  placeholder="Sistólica"
                  required
                  min="0"
                  step="1"
                />
                <span style={{ alignSelf: 'center' }}>/</span>
                <input
                  type="number"
                  name="pressaoDiastolica"
                  value={formData.pressaoDiastolica}
                  onChange={handleInputChange}
                  style={{ ...styles.input, flex: 1 }}
                  placeholder="Diastólica"
                  required
                  min="0"
                  step="1"
                />
              </div>
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
              {editingId ? 'Atualizar' : 'Cadastrar'}
            </button>
            {editingId && (
              <button 
                type="button"
                style={styles.cancelButton}
                onClick={() => {
                  setFormData({
                    data: '',
                    hora: '',
                    glicose: '',
                    pressaoSistolica: '',
                    pressaoDiastolica: '',
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

        {/* Botão para mostrar/ocultar gráfico */}
        <div style={{ textAlign: 'center', margin: '20px 0' }}>
          <button 
            onClick={() => setShowChart(!showChart)}
            style={styles.chartButton}
          >
            {showChart ? 'Ocultar Gráfico' : 'Mostrar Gráfico'}
          </button>
        </div>

        {/* Gráfico de evolução */}
        {showChart && afericoes.length > 0 && (
          <div style={styles.chartContainer}>
            <h2 style={styles.listTitle}>Evolução das Aferições</h2>
            <ResponsiveContainer width="100%" height={400}>
              <LineChart
                data={prepareChartData()}
                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="data" />
                <YAxis yAxisId="left" orientation="left" stroke="#8884d8" />
                <YAxis yAxisId="right" orientation="right" stroke="#82ca9d" />
                <Tooltip />
                <Legend />
                <Line
                  yAxisId="left"
                  type="monotone"
                  dataKey="glicose"
                  stroke="#8884d8"
                  activeDot={{ r: 8 }}
                  name="Glicose (mg/dL)"
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="pressaoSistolica"
                  stroke="#ff7300"
                  name="Pressão Sistólica"
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="pressaoDiastolica"
                  stroke="#82ca9d"
                  name="Pressão Diastólica"
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}

        {/* Lista dinâmica de aferições */}
        <div style={styles.listContainer}>
          <h2 style={styles.listTitle}>Histórico de Aferições</h2>
          
          {filteredAfericoes.length > 0 ? (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Data/Hora</th>
                    <th>Glicose (mg/dL)</th>
                    <th>Status Glicose</th>
                    <th>Pressão Arterial</th>
                    <th>Status Pressão</th>
                    <th>Observações</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredAfericoes.map((afericao) => (
                    <tr key={afericao.id}>
                      <td>{formatDate(afericao.data)} {afericao.hora}</td>
                      <td>{afericao.glicose}</td>
                      <td style={getGlicoseStyle(afericao.glicose)}>
                        {getGlicoseStatus(afericao.glicose)}
                      </td>
                      <td>{afericao.pressaoSistolica}/{afericao.pressaoDiastolica}</td>
                      <td style={getPressaoStyle(afericao.pressaoSistolica, afericao.pressaoDiastolica)}>
                        {getPressaoStatus(afericao.pressaoSistolica, afericao.pressaoDiastolica)}
                      </td>
                      <td>{afericao.observacoes || '-'}</td>
                      <td style={styles.actions}>
                        <button 
                          style={styles.editButton}
                          onClick={() => handleEdit(afericao)}
                        >
                          Editar
                        </button>
                        <button 
                          style={styles.deleteButton}
                          onClick={() => handleDelete(afericao.id)}
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
              {searchTerm ? 'Nenhuma aferição encontrada' : 'Nenhuma aferição cadastrada'}
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

// Funções auxiliares para estilos condicionais
const getGlicoseStyle = (glicose) => {
  if (glicose < 70) return { color: '#d32f2f', fontWeight: 'bold' }; // Vermelho para hipoglicemia
  if (glicose >= 70 && glicose <= 99) return { color: '#388e3c' }; // Verde para normal
  if (glicose >= 100 && glicose <= 125) return { color: '#ffa000' }; // Laranja para pré-diabetes
  return { color: '#d32f2f', fontWeight: 'bold' }; // Vermelho para diabetes
};

const getPressaoStyle = (sistolica, diastolica) => {
  if (sistolica < 120 && diastolica < 80) return { color: '#388e3c' }; // Normal - verde
  if (sistolica >= 120 && sistolica <= 129 && diastolica < 80) return { color: '#ffa000' }; // Elevada - laranja
  if ((sistolica >= 130 && sistolica <= 139) || (diastolica >= 80 && diastolica <= 89)) 
    return { color: '#f57c00', fontWeight: 'bold' }; // Hipertensão Estágio 1 - laranja escuro
  if (sistolica >= 140 || diastolica >= 90) return { color: '#d32f2f', fontWeight: 'bold' }; // Hipertensão Estágio 2 - vermelho
  return { color: '#d32f2f', fontWeight: 'bold' }; // Crise Hipertensiva - vermelho
};

// Estilos
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
  chartButton: {
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem',
    '&:hover': {
      backgroundColor: '#388e3c',
    },
  },
  chartContainer: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '8px',
    marginBottom: '20px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
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

export default Afericoes;