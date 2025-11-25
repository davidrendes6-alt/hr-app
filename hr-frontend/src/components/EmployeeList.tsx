import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { hrService } from '../api/hrService';
import type { PublicProfile } from '../types';

const EmployeeList = () => {
  const [employees, setEmployees] = useState<PublicProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const data = await hrService.getAllEmployees();
        setEmployees(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load employees');
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, []);

  const filteredEmployees = employees.filter((emp) => {
    const term = searchTerm.toLowerCase();
    return (
      emp.name.toLowerCase().includes(term) ||
      emp.email.toLowerCase().includes(term) ||
      emp.department?.toLowerCase().includes(term) ||
      emp.position?.toLowerCase().includes(term)
    );
  });

  if (loading) return <div className="loading">Loading employees...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="employee-list-container">
      <h2>Employee Directory</h2>
      
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search by name, email, department, or position..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>

      {filteredEmployees.length === 0 ? (
        <p>No employees found.</p>
      ) : (
        <div className="employee-grid">
          {filteredEmployees.map((employee) => (
            <Link
              key={employee.id}
              to={`/coworker/${employee.id}`}
              className="employee-card"
            >
              <h3>{employee.name}</h3>
              <p className="employee-email">{employee.email}</p>
              {employee.department && (
                <p className="employee-detail">📁 {employee.department}</p>
              )}
              {employee.position && (
                <p className="employee-detail">💼 {employee.position}</p>
              )}
              <>{"------------------------------------"}</>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default EmployeeList;
