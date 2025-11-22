import { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { hrService } from '../api/hrService';
import type { AbsenceRequest } from '../types';

const ManagerProfile = () => {
  const { user } = useAuth();
  const [requests, setRequests] = useState<AbsenceRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchRequests = async () => {
      try {
        const data = await hrService.getPendingAbsenceRequests();
        setRequests(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load absence requests');
      } finally {
        setLoading(false);
      }
    };

    if (user?.role === 'manager') {
      fetchRequests();
    }
  }, [user]);

  const handleApprove = async (id: string) => {
    try {
      await hrService.approveAbsenceRequest(id);
      setRequests(requests.filter(req => req.id !== id));
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to approve request');
    }
  };

  const handleReject = async (id: string) => {
    try {
      await hrService.rejectAbsenceRequest(id);
      setRequests(requests.filter(req => req.id !== id));
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to reject request');
    }
  };

  if (user?.role !== 'manager') {
    return <div className="error">Access denied. Manager role required.</div>;
  }

  if (loading) return <div className="loading">Loading requests...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="manager-dashboard">
      <h1>Manager Dashboard</h1>
      <h2>Pending Absence Requests</h2>
      {requests.length === 0 ? (
        <p>No pending requests</p>
      ) : (
        <div className="requests-list">
          {requests.map((request) => (
            <div key={request.id} className="request-card">
              <h3>{request.employeeName}</h3>
              <p><strong>Start:</strong> {new Date(request.startDate).toLocaleDateString()}</p>
              <p><strong>End:</strong> {new Date(request.endDate).toLocaleDateString()}</p>
              <p><strong>Reason:</strong> {request.reason}</p>
              <p><strong>Status:</strong> {request.status}</p>
              <div className="request-actions">
                <button onClick={() => handleApprove(request.id)} className="approve-btn">
                  Approve
                </button>
                <button onClick={() => handleReject(request.id)} className="reject-btn">
                  Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ManagerProfile;
