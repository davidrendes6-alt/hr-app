import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to="/">
            <span className="brand-icon">🏢</span>
            <span className="brand-text">HR Portal</span>
          </Link>
        </div>
        
        {user && (
          <div className="navbar-menu">
            <div className="nav-links">
              <Link 
                to="/profile" 
                className={`nav-link ${isActive('/profile') ? 'active' : ''}`}
              >
                <span className="nav-icon">👤</span>
                <span>My Profile</span>
              </Link>
              
              <Link 
                to="/employees" 
                className={`nav-link ${isActive('/employees') ? 'active' : ''}`}
              >
                <span className="nav-icon">👥</span>
                <span>Employees</span>
              </Link>
              
              <Link 
                to="/absence-request" 
                className={`nav-link ${isActive('/absence-request') ? 'active' : ''}`}
              >
                <span className="nav-icon">📅</span>
                <span>Request Absence</span>
              </Link>
              
              {user.role === 'manager' && (
                <Link 
                  to="/manager" 
                  className={`nav-link manager-link ${isActive('/manager') ? 'active' : ''}`}
                >
                  <span className="nav-icon">⚡</span>
                  <span>Dashboard</span>
                </Link>
              )}
            </div>
            
            <div className="navbar-user">
              <div className="user-info">
                <span className="user-avatar">
                  {user.name.charAt(0).toUpperCase()}
                </span>
                <div className="user-details">
                  <span className="user-name">{user.name}</span>
                  <span>{" "}</span>
                  <span className="user-role">{user.role}</span>
                </div>
              </div>
              <button onClick={handleLogout} className="logout-btn">
                <span>Logout</span>
                <span className="logout-icon">🚪</span>
              </button>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
