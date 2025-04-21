import logo from './medcontrol.png';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Controle Inteligente para Tratamento Médico.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Projeto Integrador III - UNIVESP 2025 
        </a>
      </header>
    </div>
  );
}

export default App;
