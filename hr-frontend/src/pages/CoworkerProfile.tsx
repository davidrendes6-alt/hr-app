import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { hrService } from '../api/hrService';
import type { EmployeeProfile, Feedback, UpdateProfileRequest } from '../types';

const CoworkerProfile = () => {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const [coworker, setCoworker] = useState<EmployeeProfile | null>(null);
  const [feedback, setFeedback] = useState<Feedback[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Edit mode state
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<UpdateProfileRequest>({});
  const [saving, setSaving] = useState(false);
  
  // Feedback form state
  const [showFeedbackForm, setShowFeedbackForm] = useState(false);
  const [feedbackContent, setFeedbackContent] = useState('');
  const [useAI, setUseAI] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const isManager = user?.role === 'manager';

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      
      try {
        const [coworkerData, feedbackData] = await Promise.all([
          hrService.getProfileById(id),
          hrService.getFeedbackForProfile(id),
        ]);
        
        setCoworker(coworkerData as EmployeeProfile);
        setFeedback(feedbackData);
        
        // Initialize form data
        setFormData({
          name: coworkerData.name,
          email: coworkerData.email,
          department: coworkerData.department,
          position: coworkerData.position,
          phoneNumber: coworkerData.phoneNumber,
          address: coworkerData.address,
          emergencyContact: coworkerData.emergencyContact,
          salary: coworkerData.salary,
        });
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load coworker profile');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const handleSave = async () => {
    if (!id || !coworker) return;
    
    setSaving(true);
    setError('');

    try {
      const updatedProfile = await hrService.updateProfile(id, formData);
      setCoworker(updatedProfile);
      setIsEditing(false);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    if (coworker) {
      setFormData({
        name: coworker.name,
        email: coworker.email,
        department: coworker.department,
        position: coworker.position,
        phoneNumber: coworker.phoneNumber,
        address: coworker.address,
        emergencyContact: coworker.emergencyContact,
        salary: coworker.salary,
      });
    }
    setError('');
  };

  const handleSubmitFeedback = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !feedbackContent.trim()) return;
    
    setSubmitting(true);
    setError('');

    try {
      const newFeedback = await hrService.createFeedback(id, feedbackContent, useAI);
      setFeedback([newFeedback, ...feedback]);
      setFeedbackContent('');
      setUseAI(false);
      setShowFeedbackForm(false);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit feedback');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error && !coworker) return <div className="error">{error}</div>;
  if (!coworker) return <div>No coworker data available</div>;

  return (
    <div className="coworker-profile-container">
      <div className="profile-header">
        <h1>{isManager ? 'Employee Profile' : 'Coworker Profile'}</h1>
        {isManager && !isEditing && (
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
              <span>{coworker.name}</span>
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
              <span>{coworker.email}</span>
            )}
          </div>
          <div className="profile-field">
            <label>Role:</label>
            <span>{coworker.role}</span>
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
              <span>{coworker.department || 'N/A'}</span>
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
              <span>{coworker.position || 'N/A'}</span>
            )}
          </div>
          {coworker.hireDate && (
            <div className="profile-field">
              <label>Hire Date:</label>
              <span>{new Date(coworker.hireDate).toLocaleDateString()}</span>
            </div>
          )}
        </div>

        {isManager && (
          <>
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
                  <span>{coworker.phoneNumber || 'N/A'}</span>
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
                  <span>{coworker.address || 'N/A'}</span>
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
                  <span>{coworker.emergencyContact || 'N/A'}</span>
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
                  <span>{coworker.salary ? `$${coworker.salary.toLocaleString()}` : 'N/A'}</span>
                )}
              </div>
            </div>

            {coworker.ssn && (
              <div className="profile-section">
                <h2>Sensitive Information</h2>
                <div className="profile-field">
                  <label>SSN:</label>
                  <span>***-**-{coworker.ssn.slice(-4)}</span>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      <div className="feedback-section">
        <div className="feedback-header">
          <h2>Feedback</h2>
          {!showFeedbackForm && (
            <button 
              onClick={() => setShowFeedbackForm(true)} 
              className="add-feedback-btn"
            >
              Leave Feedback
            </button>
          )}
        </div>

        {error && <div className="error-message">{error}</div>}

        {showFeedbackForm && (
          <div className="feedback-form-container">
            <form onSubmit={handleSubmitFeedback} className="feedback-form">
              <div className="form-group">
                <label htmlFor="feedback">Your Feedback:</label>
                <textarea
                  id="feedback"
                  value={feedbackContent}
                  onChange={(e) => setFeedbackContent(e.target.value)}
                  placeholder="Write your feedback here..."
                  rows={5}
                  required
                  disabled={submitting}
                />
              </div>
              
              <div className="form-group checkbox-group">
                <label>
                  <input
                    type="checkbox"
                    checked={useAI}
                    onChange={(e) => setUseAI(e.target.checked)}
                    disabled={submitting}
                  />
                  Polish with AI (improve grammar and tone)
                </label>
              </div>

              <div className="form-actions">
                <button 
                  type="submit" 
                  disabled={submitting || !feedbackContent.trim()}
                  className="submit-btn"
                >
                  {submitting ? 'Submitting...' : 'Submit Feedback'}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowFeedbackForm(false);
                    setFeedbackContent('');
                    setUseAI(false);
                  }}
                  disabled={submitting}
                  className="cancel-btn"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        <div className="feedback-list">
          {feedback.length === 0 ? (
            <p className="no-feedback">No feedback yet.</p>
          ) : (
            feedback.map((fb) => (
              <div key={fb.id} className="feedback-item">
                <div className="feedback-header-info">
                  <strong>{fb.authorName}</strong>
                  <span className="feedback-date">
                    {new Date(fb.createdAt).toLocaleDateString()}
                  </span>
                  {fb.isPolished && (
                    <span className="ai-badge" title="Enhanced with AI">
                      ✨ AI Enhanced
                    </span>
                  )}
                </div>
                <p className="feedback-content">{fb.content}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default CoworkerProfile;
