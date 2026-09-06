import { useState, useEffect } from 'react';
import './App.css';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const [activeTab, setActiveTab] = useState('create');
  const [serverStatus, setServerStatus] = useState('checking');

  // Form States
  const [accountType, setAccountType] = useState('savings');
  const [accNum, setAccNum] = useState('');
  const [name, setName] = useState('');
  const [balance, setBalance] = useState('');
  
  // Deposit / Withdraw state
  const [amount, setAmount] = useState('');

  // Transfer state
  const [fromAcc, setFromAcc] = useState('');
  const [toAcc, setToAcc] = useState('');

  // Statement / Search state
  const [searchAcc, setSearchAcc] = useState('');
  const [accountDetails, setAccountDetails] = useState(null);

  // Response Feedback state
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);

  // Check Backend Connection Status on Mount
  useEffect(() => {
    checkHealth();
  }, []);

  const checkHealth = async () => {
    try {
      const res = await fetch(`${API_BASE}/health`);
      if (res.ok) {
        setServerStatus('online');
      } else {
        setServerStatus('offline');
      }
    } catch {
      setServerStatus('offline');
    }
  };

  const notify = (msg, error = false) => {
    setMessage(msg);
    setIsError(error);
    checkHealth();
  };

  // 1. Create Account
  const handleCreateAccount = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    
    try {
      const endpoint = accountType === 'savings' ? 'accounts/savings' : 'accounts/current';
      const response = await fetch(`${API_BASE}/${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ accNum, name, balance }).toString(),
      });

      const data = await response.text();
      if (response.ok) {
        notify(data, false);
        setAccNum('');
        setName('');
        setBalance('');
      } else {
        notify(data, true);
      }
    } catch (err) {
      notify('Failed to connect to backend server. Make sure BankServer is running on port 8080.', true);
    } finally {
      setLoading(false);
    }
  };

  // 2. Deposit
  const handleDeposit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      const response = await fetch(`${API_BASE}/accounts/deposit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ accNum, amount }).toString(),
      });

      const data = await response.text();
      if (response.ok) {
        notify(data, false);
        setAmount('');
      } else {
        notify(data, true);
      }
    } catch (err) {
      notify('Failed to execute deposit. Backend connection error.', true);
    } finally {
      setLoading(false);
    }
  };

  // 3. Withdraw
  const handleWithdraw = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      const response = await fetch(`${API_BASE}/accounts/withdraw`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ accNum, amount }).toString(),
      });

      const data = await response.text();
      if (response.ok) {
        notify(data, false);
        setAmount('');
      } else {
        notify(data, true);
      }
    } catch (err) {
      notify('Failed to execute withdrawal. Backend connection error.', true);
    } finally {
      setLoading(false);
    }
  };

  // 4. Transfer
  const handleTransfer = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      const response = await fetch(`${API_BASE}/accounts/transfer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ fromAcc, toAcc, amount }).toString(),
      });

      const data = await response.text();
      if (response.ok) {
        notify(data, false);
        setFromAcc('');
        setToAcc('');
        setAmount('');
      } else {
        notify(data, true);
      }
    } catch (err) {
      notify('Failed to execute transfer. Backend connection error.', true);
    } finally {
      setLoading(false);
    }
  };

  // 5. Statement / Account Query
  const handleFetchStatement = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    setAccountDetails(null);

    try {
      const response = await fetch(`${API_BASE}/accounts/statement?accNum=${searchAcc}`);
      if (response.ok) {
        const data = await response.json();
        setAccountDetails(data);
        notify('Account statement retrieved successfully.', false);
      } else {
        const errText = await response.text();
        notify(errText, true);
      }
    } catch (err) {
      notify('Failed to fetch account statement. Backend connection error.', true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container">
      {/* Top Navigation Bar */}
      <header className="header">
        <div className="brand">
          <span className="brand-logo">🏦</span>
          <div>
            <h1>Apex Banking Portal</h1>
            <p className="subtitle">Java & PostgreSQL Backend Integration</p>
          </div>
        </div>
        <div className="status-badge-container">
          <span className={`status-indicator ${serverStatus}`} />
          <span className="status-text">
            Backend: {serverStatus === 'online' ? 'Connected (Port 8080)' : serverStatus === 'offline' ? 'Disconnected' : 'Checking...'}
          </span>
          <button className="retry-btn" onClick={checkHealth}>Re-check</button>
        </div>
      </header>

      {/* Main Dashboard Workspace */}
      <div className="dashboard-card">
        {/* Navigation Tabs */}
        <nav className="tab-nav">
          <button 
            className={`tab-btn ${activeTab === 'create' ? 'active' : ''}`}
            onClick={() => { setActiveTab('create'); setMessage(''); }}
          >
            Create Account
          </button>
          <button 
            className={`tab-btn ${activeTab === 'deposit' ? 'active' : ''}`}
            onClick={() => { setActiveTab('deposit'); setMessage(''); }}
          >
            Deposit
          </button>
          <button 
            className={`tab-btn ${activeTab === 'withdraw' ? 'active' : ''}`}
            onClick={() => { setActiveTab('withdraw'); setMessage(''); }}
          >
            Withdraw
          </button>
          <button 
            className={`tab-btn ${activeTab === 'transfer' ? 'active' : ''}`}
            onClick={() => { setActiveTab('transfer'); setMessage(''); }}
          >
            Transfer
          </button>
          <button 
            className={`tab-btn ${activeTab === 'statement' ? 'active' : ''}`}
            onClick={() => { setActiveTab('statement'); setMessage(''); }}
          >
            Account Statement
          </button>
        </nav>

        {/* Dynamic Content Panel */}
        <div className="tab-content">
          {/* Create Account Form */}
          {activeTab === 'create' && (
            <form onSubmit={handleCreateAccount} className="form-grid">
              <h2>Open New Account</h2>
              
              <div className="form-group">
                <label>Account Type</label>
                <div className="radio-group">
                  <label className={`radio-card ${accountType === 'savings' ? 'selected' : ''}`}>
                    <input 
                      type="radio" 
                      name="accType" 
                      value="savings"
                      checked={accountType === 'savings'} 
                      onChange={() => setAccountType('savings')} 
                    />
                    Savings Account
                  </label>
                  <label className={`radio-card ${accountType === 'current' ? 'selected' : ''}`}>
                    <input 
                      type="radio" 
                      name="accType" 
                      value="current"
                      checked={accountType === 'current'} 
                      onChange={() => setAccountType('current')} 
                    />
                    Current Account
                  </label>
                </div>
              </div>

              <div className="form-group">
                <label>Account Number</label>
                <input 
                  type="text"
                  placeholder="e.g. ACC1001"
                  value={accNum}
                  onChange={(e) => setAccNum(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Account Holder Name</label>
                <input 
                  type="text"
                  placeholder="e.g. Jane Doe"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Initial Deposit ($)</label>
                <input 
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="e.g. 500.00"
                  value={balance}
                  onChange={(e) => setBalance(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="primary-btn" disabled={loading}>
                {loading ? 'Creating...' : 'Create Account'}
              </button>
            </form>
          )}

          {/* Deposit Form */}
          {activeTab === 'deposit' && (
            <form onSubmit={handleDeposit} className="form-grid">
              <h2>Deposit Funds</h2>

              <div className="form-group">
                <label>Account Number</label>
                <input 
                  type="text"
                  placeholder="e.g. ACC1001"
                  value={accNum}
                  onChange={(e) => setAccNum(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Deposit Amount ($)</label>
                <input 
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="e.g. 250.00"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="primary-btn deposit-btn" disabled={loading}>
                {loading ? 'Processing...' : 'Deposit Funds'}
              </button>
            </form>
          )}

          {/* Withdraw Form */}
          {activeTab === 'withdraw' && (
            <form onSubmit={handleWithdraw} className="form-grid">
              <h2>Withdraw Funds</h2>

              <div className="form-group">
                <label>Account Number</label>
                <input 
                  type="text"
                  placeholder="e.g. ACC1001"
                  value={accNum}
                  onChange={(e) => setAccNum(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Withdrawal Amount ($)</label>
                <input 
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="e.g. 100.00"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="primary-btn withdraw-btn" disabled={loading}>
                {loading ? 'Processing...' : 'Withdraw Funds'}
              </button>
            </form>
          )}

          {/* Transfer Form */}
          {activeTab === 'transfer' && (
            <form onSubmit={handleTransfer} className="form-grid">
              <h2>Transfer Funds</h2>

              <div className="form-group">
                <label>Source Account Number</label>
                <input 
                  type="text"
                  placeholder="e.g. ACC1001"
                  value={fromAcc}
                  onChange={(e) => setFromAcc(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Destination Account Number</label>
                <input 
                  type="text"
                  placeholder="e.g. ACC1002"
                  value={toAcc}
                  onChange={(e) => setToAcc(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Transfer Amount ($)</label>
                <input 
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="e.g. 150.00"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="primary-btn transfer-btn" disabled={loading}>
                {loading ? 'Transferring...' : 'Execute Transfer'}
              </button>
            </form>
          )}

          {/* Account Statement */}
          {activeTab === 'statement' && (
            <div className="statement-container">
              <h2>Account Statement & Lookup</h2>
              <form onSubmit={handleFetchStatement} className="search-form">
                <input 
                  type="text"
                  placeholder="Enter Account Number (e.g. ACC1001)"
                  value={searchAcc}
                  onChange={(e) => setSearchAcc(e.target.value)}
                  required
                />
                <button type="submit" className="primary-btn" disabled={loading}>
                  {loading ? 'Searching...' : 'Lookup Account'}
                </button>
              </form>

              {accountDetails && (
                <div className="account-card">
                  <div className="account-card-header">
                    <h3>Account Summary</h3>
                    <span className="badge">Active</span>
                  </div>
                  <div className="account-details-grid">
                    <div className="detail-item">
                      <span className="label">Account Number</span>
                      <span className="value">{accountDetails.accountNumber}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Holder Name</span>
                      <span className="value">{accountDetails.holderName}</span>
                    </div>
                    <div className="detail-item full-width">
                      <span className="label">Current Balance</span>
                      <span className="value balance-val">${accountDetails.balance?.toFixed(2)}</span>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Feedback Message Notification */}
          {message && (
            <div className={`notification-banner ${isError ? 'error' : 'success'}`}>
              <span className="icon">{isError ? '⚠️' : '✅'}</span>
              <span>{message}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;