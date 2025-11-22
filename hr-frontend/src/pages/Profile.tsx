import { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { hrService } from '../api/hrService';
import type { EmployeeProfile, UpdateProfileRequest } from '../types';

const Profile = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState<EmployeeProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<UpdateProfileRequest>({});
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await hrService.getMyProfile();
        setProfile(data);
        setFormData({
          name: data.name,
          email: data.email,
          department: data.department,
          position: data.position,
          phoneNumber: data.phoneNumber,
          address: data.address,
          emergencyContact: data.emergencyContact,
          salary: data.salary,
        });
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load profile');
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleSave = async () => {
    if (!profile) return;
    
    setSaving(true);
    setError('');

    try {
      const updatedProfile = await hrService.updateProfile(profile.id, formData);
      setProfile(updatedProfile);
      setIsEditing(false);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    if (profile) {
      setFormData({
        name: profile.name,
        email: profile.email,
        department: profile.department,
        position: profile.position,
        phoneNumber: profile.phoneNumber,
        address: profile.address,
        emergencyContact: profile.emergencyContact,
        salary: profile.salary,
      });
    }
  };

  const canEdit = user?.role === 'manager' || user?.id === profile?.id;

  if (loading) return <div className="loading">Loading profile...</div>;
  if (error && !profile) return <div className="error">{error}</div>;
  if (!profile) return <div>No profile data available</div>;

  return (
    <div className="profile-container">
      <div className="profile-header">
        <h1>My Profile</h1>
        {canEdit && !isEditing && (
          <button onClick={() => setIsEditing(true)} className="edit-btn">
            Edit Profile
          </button>
        )}
        {isEditing && (
          <div className="edit-actions">
            <button onClick={handleSave} disabled={saving} className="save-btn">
              {saving ? 'Saving...' : 'Save'}
            </button>
            <button onClick={handleCancel} disabled={saving} className="cancel-btn">
              Cancel
            </button>
          </div>
        )}
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="profile-card">
        <div className="profile-section">
          <h2>Basic Information</h2>
          
          <div className="profile-field">
            <label>Name:</label>
            {isEditing ? (
              <input
                type="text"
                value={formData.name || ''}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              />
            ) : (
              <span>{profile.name}</span>
            )}
          </div>

          <div className="profile-field">
            <label>Email:</label>
            {isEditing ? (
              <input
                type="email"
                value={formData.email || ''}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
            ) : (
              <span>{profile.email}</span>
            )}
          </div>

          <div className="profile-field">
            <label>Role:</label>
            <span>{profile.role}</span>
          </div>

          <div className="profile-field">
            <label>Department:</label>
            {isEditing ? (
              <input
                type="text"
                value={formData.department || ''}
                onChange={(e) => setFormData({ ...formData, department: e.target.value })}
              />
            ) : (
              <span>{profile.department || 'N/A'}</span>
            )}
          </div>

          <div className="profile-field">
            <label>Position:</label>
            {isEditing ? (
              <input
                type="text"
                value={formData.position || ''}
                onChange={(e) => setFormData({ ...formData, position: e.target.value })}
              />
            ) : (
              <span>{profile.position || 'N/A'}</span>
            )}
          </div>

          {profile.hireDate && (
            <div className="profile-field">
              <label>Hire Date:</label>
              <span>{new Date(profile.hireDate).toLocaleDateString()}</span>
            </div>
          )}
        </div>

        <div className="profile-section">
          <h2>Contact Information</h2>
          
          <div className="profile-field">
            <label>Phone:</label>
            {isEditing ? (
              <input
                type="tel"
                value={formData.phoneNumber || ''}
                onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
              />
            ) : (
              <span>{profile.phoneNumber || 'N/A'}</span>
            )}
          </div>

          <div className="profile-field">
            <label>Address:</label>
            {isEditing ? (
              <input
                type="text"
                value={formData.address || ''}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
              />
            ) : (
              <span>{profile.address || 'N/A'}</span>
            )}
          </div>

          <div className="profile-field">
            <label>Emergency Contact:</label>
            {isEditing ? (
              <input
                type="text"
                value={formData.emergencyContact || ''}
                onChange={(e) => setFormData({ ...formData, emergencyContact: e.target.value })}
              />
            ) : (
              <span>{profile.emergencyContact || 'N/A'}</span>
            )}
          </div>
        </div>

        <div className="profile-section">
          <h2>Compensation</h2>
          
          <div className="profile-field">
            <label>Salary:</label>
            {isEditing ? (
              <input
                type="number"
                value={formData.salary || ''}
                onChange={(e) => setFormData({ ...formData, salary: parseFloat(e.target.value) })}
              />
            ) : (
              <span>{profile.salary ? `$${profile.salary.toLocaleString()}` : 'N/A'}</span>
            )}
          </div>
        </div>

        {profile.ssn && (
          <div className="profile-section">
            <h2>Sensitive Information</h2>
            <div className="profile-field">
              <label>SSN:</label>
              <span>***-**-{profile.ssn.slice(-4)}</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
