import React, { useState, useEffect, useRef } from 'react'
import { 
  Search, MapPin, Briefcase, DollarSign, Upload, FileText, 
  CheckCircle, Clock, X, AlertTriangle, User, Shield, Key, 
  LogOut, Check, Building, Trash2, ChevronRight, Eye, Info, 
  PlusCircle, RefreshCw, BarChart2, Users, FileSignature, 
  FileCode, Menu, Globe, Calendar, ArrowRight, UserCheck, UserX
} from 'lucide-react'

// Backend Base URL helper (Vite proxies /api to http://localhost:8080/api)
const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

function App() {
  // Authentication state
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('cp_user')
    return saved ? JSON.parse(saved) : null
  })

  // Navigation / View State
  // Views: 'jobs', 'my-applications', 'posted-jobs', 'resumes', 'admin-reports', 'admin-users'
  const [view, setView] = useState('jobs')

  // Auth Modals
  const [showAuthModal, setShowAuthModal] = useState(null) // 'login', 'register' or null
  const [authRole, setAuthRole] = useState('ROLE_APPLICANT')
  const [authEmail, setAuthEmail] = useState('')
  const [authPassword, setAuthPassword] = useState('')
  const [authFirstName, setAuthFirstName] = useState('')
  const [authLastName, setAuthLastName] = useState('')
  const [authPhone, setAuthPhone] = useState('')

  // Public Job Search & Filters State
  const [jobs, setJobs] = useState([])
  const [loadingJobs, setLoadingJobs] = useState(false)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [searchLocation, setSearchLocation] = useState('')
  const [searchJobType, setSearchJobType] = useState('')
  const [searchRemote, setSearchRemote] = useState(false)
  const [searchSalaryMin, setSearchSalaryMin] = useState('')
  const [selectedJob, setSelectedJob] = useState(null)

  // Applicant Portal States
  const [myApplications, setMyApplications] = useState([])
  const [myResumes, setMyResumes] = useState([])
  const [resumeFile, setResumeFile] = useState(null)
  const [resumeTitle, setResumeTitle] = useState('')
  const [isUploadingResume, setIsUploadingResume] = useState(false)
  const [coverLetter, setCoverLetter] = useState('')
  const [proposedSalary, setProposedSalary] = useState('')
  const [selectedResumeId, setSelectedResumeId] = useState('')
  const [showApplyModal, setShowApplyModal] = useState(null) // Job object or null

  // Recruiter Portal States
  const [myPostedJobs, setMyPostedJobs] = useState([])
  const [selectedJobApplicants, setSelectedJobApplicants] = useState([])
  const [selectedJobForApplicants, setSelectedJobForApplicants] = useState(null)
  const [showPostJobModal, setShowPostJobModal] = useState(false)
  
  // Job Post Form
  const [jobPostTitle, setJobPostTitle] = useState('')
  const [jobPostCompany, setJobPostCompany] = useState('')
  const [jobPostDesc, setJobPostDesc] = useState('')
  const [jobPostReqs, setJobPostReqs] = useState('')
  const [jobPostLoc, setJobPostLoc] = useState('')
  const [jobPostType, setJobPostType] = useState('FULL_TIME')
  const [jobPostRemote, setJobPostRemote] = useState(false)
  const [jobPostSalary, setJobPostSalary] = useState('')
  
  // Applicant Status Update Form
  const [selectedApplicationForStatus, setSelectedApplicationForStatus] = useState(null)
  const [newApplicationStatus, setNewApplicationStatus] = useState('SHORTLISTED')
  const [statusFeedback, setStatusFeedback] = useState('')

  // Admin Portal States
  const [adminUsers, setAdminUsers] = useState([])
  const [adminReports, setAdminReports] = useState(null)
  const [adminSearchUser, setAdminSearchUser] = useState('')

  // UI Toast State
  const [toasts, setToasts] = useState([])

  // Helper: Toast alerts
  const showToast = (message, type = 'success') => {
    const id = Date.now()
    setToasts(prev => [...prev, { id, message, type }])
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id))
    }, 4000)
  }

  // Fetch Public Jobs
  const fetchJobs = async () => {
    setLoadingJobs(true)
    try {
      let query = []
      if (searchKeyword) query.push(`keyword=${encodeURIComponent(searchKeyword)}`)
      if (searchLocation) query.push(`location=${encodeURIComponent(searchLocation)}`)
      if (searchJobType) query.push(`jobType=${searchJobType}`)
      if (searchRemote) query.push(`remote=true`)
      if (searchSalaryMin) query.push(`salaryMin=${searchSalaryMin}`)
      
      const queryString = query.length ? `?${query.join('&')}` : ''
      const res = await fetch(`${API_BASE}/jobs${queryString}`)
      const data = await res.json()
      
      if (data.success) {
        setJobs(data.data.content || [])
      } else {
        showToast(data.message || 'Failed to fetch jobs', 'error')
      }
    } catch (err) {
      showToast('Network error while loading jobs', 'error')
    } finally {
      setLoadingJobs(false)
    }
  }

  // Trigger search on mount and filter changes
  useEffect(() => {
    fetchJobs()
  }, [searchJobType, searchRemote])

  // Login handler
  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: authEmail, password: authPassword })
      })
      const data = await res.json()
      if (data.success) {
        // Save user state
        const loggedUser = {
          email: data.data.email,
          firstName: data.data.firstName,
          lastName: data.data.lastName,
          role: data.data.roles[0], // primary role
          token: data.data.token
        }
        setUser(loggedUser)
        localStorage.setItem('cp_user', JSON.stringify(loggedUser))
        showToast(`Welcome back, ${loggedUser.firstName}!`)
        setShowAuthModal(null)
        // Reset auth fields
        setAuthEmail('')
        setAuthPassword('')
        // Redirect depending on role
        if (loggedUser.role === 'ROLE_ADMIN') {
          setView('admin-reports')
        } else if (loggedUser.role === 'ROLE_RECRUITER') {
          setView('posted-jobs')
        } else {
          setView('jobs')
        }
      } else {
        showToast(data.message || 'Login failed. Please check credentials.', 'error')
      }
    } catch (err) {
      showToast('Failed to connect to backend auth service', 'error')
    }
  }

  // Register handler
  const handleRegister = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          firstName: authFirstName,
          lastName: authLastName,
          email: authEmail,
          password: authPassword,
          phone: authPhone,
          role: authRole
        })
      })
      const data = await res.json()
      if (data.success) {
        showToast('Registration successful! Please log in now.')
        setShowAuthModal('login')
        setAuthFirstName('')
        setAuthLastName('')
        setAuthPhone('')
      } else {
        showToast(data.message || 'Registration failed', 'error')
      }
    } catch (err) {
      showToast('Connection failed during registration', 'error')
    }
  }

  // Logout handler
  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('cp_user')
    showToast('Logged out successfully.')
    setView('jobs')
  }

  // Helper headers with auth token
  const getAuthHeaders = () => {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${user?.token}`
    }
  }

  // Fetch Applicant Resumes
  const fetchMyResumes = async () => {
    if (!user || user.role !== 'ROLE_APPLICANT') return
    try {
      const res = await fetch(`${API_BASE}/resumes/my`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      const data = await res.json()
      if (data.success) {
        setMyResumes(data.data || [])
        // Set first primary resume as selected, or the first in list
        const primary = data.data.find(r => r.primary)
        if (primary) setSelectedResumeId(primary.id)
        else if (data.data.length) setSelectedResumeId(data.data[0].id)
      }
    } catch (err) {
      showToast('Failed to fetch resumes', 'error')
    }
  }

  // Upload Resume
  const handleUploadResume = async (e) => {
    e.preventDefault()
    if (!resumeFile) {
      showToast('Please select a file to upload', 'error')
      return
    }
    setIsUploadingResume(true)
    const formData = new FormData()
    formData.append('file', resumeFile)

    try {
      const res = await fetch(`${API_BASE}/resumes/upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${user?.token}`
        },
        body: formData
      })
      const data = await res.json()
      if (data.success) {
        showToast('Resume uploaded successfully!')
        setResumeFile(null)
        setResumeTitle('')
        fetchMyResumes()
      } else {
        showToast(data.message || 'Upload failed', 'error')
      }
    } catch (err) {
      showToast('Upload error. Check file size (max 5MB) and type (PDF/Word).', 'error')
    } finally {
      setIsUploadingResume(false)
    }
  }

  // Set Resume Primary
  const handleSetPrimaryResume = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/resumes/${id}/primary`, {
        method: 'PATCH',
        headers: getAuthHeaders()
      })
      const data = await res.json()
      if (data.success) {
        showToast('Primary resume updated!')
        fetchMyResumes()
      } else {
        showToast(data.message, 'error')
      }
    } catch (err) {
      showToast('Failed to set primary resume', 'error')
    }
  }

  // Delete Resume
  const handleDeleteResume = async (id) => {
    if (!confirm('Are you sure you want to delete this resume?')) return
    try {
      const res = await fetch(`${API_BASE}/resumes/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
      })
      const data = await res.json()
      if (data.success) {
        showToast('Resume deleted successfully')
        fetchMyResumes()
      } else {
        showToast(data.message, 'error')
      }
    } catch (err) {
      showToast('Failed to delete resume', 'error')
    }
  }

  // Apply for Job
  const handleApplySubmit = async (e) => {
    e.preventDefault()
    if (!selectedResumeId) {
      showToast('Please upload and select a resume to apply', 'error')
      return
    }
    try {
      const res = await fetch(`${API_BASE}/applicant/apply`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({
          jobId: showApplyModal.id,
          resumeId: Number(selectedResumeId),
          coverLetter: coverLetter,
          proposedSalary: proposedSalary ? Number(proposedSalary) : null
        })
      })
      const data = await res.json()
      if (data.success) {
        showToast('Application submitted successfully!')
        setShowApplyModal(null)
        setCoverLetter('')
        setProposedSalary('')
        fetchMyApplications()
      } else {
        showToast(data.message || 'Failed to submit application', 'error')
      }
    } catch (err) {
      showToast('Network error during job application', 'error')
    }
  }

  // Fetch Applicant applications
  const fetchMyApplications = async () => {
    if (!user || user.role !== 'ROLE_APPLICANT') return
    try {
      const res = await fetch(`${API_BASE}/applicant/applications`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      const data = await res.json()
      if (data.success) {
        setMyApplications(data.data.content || [])
      }
    } catch (err) {
      showToast('Failed to fetch applications', 'error')
    }
  }

  // Fetch Recruiter Posted Jobs
  const fetchMyPostedJobs = async () => {
    if (!user || user.role !== 'ROLE_RECRUITER') return
    try {
      const res = await fetch(`${API_BASE}/recruiter/jobs`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      const data = await res.json()
      if (data.success) {
        setMyPostedJobs(data.data.content || [])
      }
    } catch (err) {
      showToast('Failed to fetch posted jobs', 'error')
    }
  }

  // Fetch Applicants for a Job (Recruiter)
  const fetchJobApplicants = async (jobId) => {
    try {
      const res = await fetch(`${API_BASE}/recruiter/jobs/${jobId}/applicants`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      const data = await res.json()
      if (data.success) {
        setSelectedJobApplicants(data.data.content || [])
      } else {
        showToast(data.message || 'Failed to load applicants', 'error')
      }
    } catch (err) {
      showToast('Failed to load applicants', 'error')
    }
  }

  // Post Job Listing (Recruiter)
  const handlePostJob = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_BASE}/jobs`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({
          title: jobPostTitle,
          company: jobPostCompany,
          description: jobPostDesc,
          requirements: jobPostReqs,
          location: jobPostLoc,
          jobType: jobPostType,
          remote: jobPostRemote,
          salary: jobPostSalary ? Number(jobPostSalary) : null
        })
      })
      const data = await res.json()
      if (data.success) {
        showToast('Job listing posted successfully!')
        setShowPostJobModal(false)
        // Reset form
        setJobPostTitle('')
        setJobPostCompany('')
        setJobPostDesc('')
        setJobPostReqs('')
        setJobPostLoc('')
        setJobPostType('FULL_TIME')
        setJobPostRemote(false)
        setJobPostSalary('')
        fetchMyPostedJobs()
      } else {
        showToast(data.message || 'Failed to post job', 'error')
      }
    } catch (err) {
      showToast('Failed to connect to backend service', 'error')
    }
  }

  // Toggle Job Status (Recruiter: ACTIVE / CLOSED)
  const handleToggleJobStatus = async (jobId, currentStatus) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'CLOSED' : 'ACTIVE'
    try {
      const res = await fetch(`${API_BASE}/jobs/${jobId}/status?status=${nextStatus}`, {
        method: 'PATCH',
        headers: getAuthHeaders()
      })
      const data = await res.json()
      if (data.success) {
        showToast(`Job status updated to ${nextStatus}`)
        fetchMyPostedJobs()
      }
    } catch (err) {
      showToast('Failed to toggle status', 'error')
    }
  }

  // Update Application hiring status (Recruiter)
  const handleUpdateHiringStatus = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_BASE}/recruiter/applications/${selectedApplicationForStatus.id}/status`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify({
          status: newApplicationStatus,
          feedback: statusFeedback
        })
      })
      const data = await res.json()
      if (data.success) {
        showToast('Applicant status updated successfully')
        setSelectedApplicationForStatus(null)
        setStatusFeedback('')
        // Refresh applicants list
        if (selectedJobForApplicants) {
          fetchJobApplicants(selectedJobForApplicants.id)
        }
      } else {
        showToast(data.message || 'Status update failed', 'error')
      }
    } catch (err) {
      showToast('Network error updating status', 'error')
    }
  }

  // Fetch Admin Users list
  const fetchAdminUsers = async (searchTerm = '') => {
    if (!user || user.role !== 'ROLE_ADMIN') return
    try {
      const query = searchTerm ? `?search=${encodeURIComponent(searchTerm)}` : ''
      const res = await fetch(`${API_BASE}/admin/users${query}`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      const data = await res.json()
      if (data.success) {
        setAdminUsers(data.data.content || [])
      }
    } catch (err) {
      showToast('Failed to load user directories', 'error')
    }
  }

  // Toggle user enablement (Admin)
  const handleToggleUserEnable = async (userId) => {
    try {
      const res = await fetch(`${API_BASE}/admin/users/${userId}/toggle`, {
        method: 'PATCH',
        headers: getAuthHeaders()
      })
      const data = await res.json()
      if (data.success) {
        showToast(data.message || 'User status toggled')
        fetchAdminUsers(adminSearchUser)
      } else {
        showToast(data.message || 'Action failed', 'error')
      }
    } catch (err) {
      showToast('Error toggling user account status', 'error')
    }
  }

  // Fetch Admin platform stats reports
  const fetchAdminReports = async () => {
    if (!user || user.role !== 'ROLE_ADMIN') return
    try {
      const res = await fetch(`${API_BASE}/admin/reports`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      const data = await res.json()
      if (data.success) {
        setAdminReports(data.data)
      }
    } catch (err) {
      showToast('Failed to load reports', 'error')
    }
  }

  // Download Resume Blob
  const handleDownloadResume = async (resumeId, filename = 'resume.pdf') => {
    try {
      const res = await fetch(`${API_BASE}/resumes/${resumeId}/download`, {
        headers: { 'Authorization': `Bearer ${user?.token}` }
      })
      if (!res.ok) throw new Error('File download failed')
      
      const blob = await res.blob()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      a.remove()
      window.URL.revokeObjectURL(url)
      showToast('File downloaded successfully')
    } catch (err) {
      showToast('Error downloading resume file', 'error')
    }
  }

  // Trigger loading details when view transitions
  useEffect(() => {
    if (view === 'resumes') {
      fetchMyResumes()
    } else if (view === 'my-applications') {
      fetchMyApplications()
    } else if (view === 'posted-jobs') {
      fetchMyPostedJobs()
    } else if (view === 'admin-users') {
      fetchAdminUsers()
    } else if (view === 'admin-reports') {
      fetchAdminReports()
    }
  }, [view])

  return (
    <div className="app-container">
      {/* Toast Notifications */}
      <div className="toast-container">
        {toasts.map(t => (
          <div key={t.id} className={`toast toast-${t.type} glass-panel`}>
            {t.type === 'success' ? <CheckCircle size={18} /> : <AlertTriangle size={18} />}
            <span>{t.message}</span>
          </div>
        ))}
      </div>

      {/* Navigation Header */}
      <header className="navbar">
        <div className="navbar-container">
          <div className="logo" onClick={() => setView('jobs')} style={{ cursor: 'pointer' }}>
            <Briefcase size={24} className="animate-pulse-glow" style={{ color: '#8b5cf6' }} />
            <span>CareerPulse</span>
          </div>
          
          <nav className="nav-links">
            <span 
              className={`nav-link ${view === 'jobs' ? 'active' : ''}`} 
              onClick={() => { setView('jobs'); setSelectedJob(null); }}
            >
              Browse Jobs
            </span>

            {user?.role === 'ROLE_APPLICANT' && (
              <>
                <span 
                  className={`nav-link ${view === 'my-applications' ? 'active' : ''}`}
                  onClick={() => setView('my-applications')}
                >
                  My Applications
                </span>
                <span 
                  className={`nav-link ${view === 'resumes' ? 'active' : ''}`}
                  onClick={() => setView('resumes')}
                >
                  Resumes
                </span>
              </>
            )}

            {user?.role === 'ROLE_RECRUITER' && (
              <span 
                className={`nav-link ${view === 'posted-jobs' ? 'active' : ''}`}
                onClick={() => { setView('posted-jobs'); setSelectedJobForApplicants(null); }}
              >
                Recruit Dashboard
              </span>
            )}

            {user?.role === 'ROLE_ADMIN' && (
              <>
                <span 
                  className={`nav-link ${view === 'admin-reports' ? 'active' : ''}`}
                  onClick={() => setView('admin-reports')}
                >
                  Stats Reports
                </span>
                <span 
                  className={`nav-link ${view === 'admin-users' ? 'active' : ''}`}
                  onClick={() => setView('admin-users')}
                >
                  Manage Users
                </span>
              </>
            )}

            {user ? (
              <div style={{ display: 'flex', alignPage: 'center', gap: '1rem', alignItems: 'center' }}>
                <span className="user-badge">
                  <User size={14} />
                  <span>{user.firstName} ({user.role.replace('ROLE_', '')})</span>
                </span>
                <button className="btn btn-secondary btn-sm" onClick={handleLogout}>
                  <LogOut size={14} />
                  <span>Logout</span>
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button className="btn btn-secondary btn-sm" onClick={() => setShowAuthModal('login')}>
                  Sign In
                </button>
                <button className="btn btn-primary btn-sm" onClick={() => { setShowAuthModal('register'); setAuthRole('ROLE_APPLICANT'); }}>
                  Get Started
                </button>
              </div>
            )}
          </nav>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="main-content">
        
        {/* PUBLIC JOB SEARCH & DETAILS */}
        {view === 'jobs' && !selectedJob && (
          <div>
            {/* Hero Section */}
            <div style={{ textAlign: 'center', margin: '3rem 0 4rem 0' }}>
              <h1 style={{ fontSize: '3rem', fontWeight: 800, marginBottom: '1rem', fontFamily: 'var(--font-heading)' }}>
                Discover Your <span style={{ background: 'var(--accent-gradient)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>Dream Career</span>
              </h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '1.15rem', maxWidth: '600px', margin: '0 auto' }}>
                Find, apply, and secure premium jobs globally. Your fast track to growth starts here.
              </p>
            </div>

            {/* Interactive Search Bar Panel */}
            <div className="glass-panel" style={{ padding: '1.5rem', marginBottom: '3rem', display: 'grid', gridTemplateColumns: '1fr 1fr 150px 140px', gap: '1rem', alignItems: 'center' }}>
              <div style={{ position: 'relative' }}>
                <Search size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
                <input 
                  type="text" 
                  placeholder="Job title, keywords, or company..." 
                  style={{ paddingLeft: '2.5rem' }} 
                  value={searchKeyword} 
                  onChange={e => setSearchKeyword(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && fetchJobs()}
                />
              </div>

              <div style={{ position: 'relative' }}>
                <MapPin size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
                <input 
                  type="text" 
                  placeholder="City, state, or country..." 
                  style={{ paddingLeft: '2.5rem' }} 
                  value={searchLocation} 
                  onChange={e => setSearchLocation(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && fetchJobs()}
                />
              </div>

              <select value={searchJobType} onChange={e => setSearchJobType(e.target.value)}>
                <option value="">Job Type</option>
                <option value="FULL_TIME">Full Time</option>
                <option value="PART_TIME">Part Time</option>
                <option value="CONTRACT">Contract</option>
                <option value="INTERNSHIP">Internship</option>
              </select>

              <button className="btn btn-primary" onClick={fetchJobs} style={{ width: '100%' }}>
                Search
              </button>
            </div>

            {/* Extra filtering controls */}
            <div style={{ display: 'flex', gap: '2rem', justifyContent: 'flex-start', alignItems: 'center', marginBottom: '2rem', flexWrap: 'wrap' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', margin: 0 }}>
                <input 
                  type="checkbox" 
                  checked={searchRemote} 
                  onChange={e => setSearchRemote(e.target.checked)} 
                  style={{ width: 'auto', transform: 'scale(1.15)' }} 
                />
                <span style={{ fontSize: '0.9rem', color: 'var(--text-primary)' }}>Remote Listings Only</span>
              </label>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Min Salary ($):</span>
                <input 
                  type="number" 
                  placeholder="e.g. 60000" 
                  style={{ width: '130px', padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}
                  value={searchSalaryMin}
                  onChange={e => setSearchSalaryMin(e.target.value)}
                  onBlur={fetchJobs}
                  onKeyDown={e => e.key === 'Enter' && fetchJobs()}
                />
              </div>
            </div>

            {/* Job Listings Grid */}
            {loadingJobs ? (
              <div style={{ textAlign: 'center', padding: '4rem 0' }}>
                <RefreshCw className="animate-spin" size={36} style={{ color: 'var(--accent-primary)', margin: '0 auto 1rem auto' }} />
                <p style={{ color: 'var(--text-secondary)' }}>Loading latest opportunities...</p>
              </div>
            ) : jobs.length === 0 ? (
              <div className="glass-panel" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
                <AlertTriangle size={36} style={{ color: 'var(--color-warning)', margin: '0 auto 1rem auto' }} />
                <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>No Jobs Found</h3>
                <p style={{ color: 'var(--text-secondary)' }}>We couldn't find any jobs matching your search criteria. Try modifying your filters or search terms.</p>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1.25rem' }}>
                {jobs.map(job => (
                  <div key={job.id} className="glass-panel" style={{ padding: '1.75rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1.5rem' }}>
                    <div style={{ flex: '1', minWidth: '300px' }}>
                      <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', marginBottom: '0.5rem' }}>
                        <span style={{ fontSize: '0.85rem', color: '#c084fc', background: 'rgba(139, 92, 246, 0.1)', padding: '0.2rem 0.6rem', borderRadius: '4px', border: '1px solid rgba(139, 92, 246, 0.2)' }}>
                          {job.company}
                        </span>
                        {job.remote && (
                          <span style={{ fontSize: '0.85rem', color: '#60a5fa', background: 'rgba(59, 130, 246, 0.1)', padding: '0.2rem 0.6rem', borderRadius: '4px', border: '1px solid rgba(59, 130, 246, 0.2)' }}>
                            Remote
                          </span>
                        )}
                        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                          {job.jobType.replace('_', ' ')}
                        </span>
                      </div>
                      <h3 style={{ fontSize: '1.35rem', marginBottom: '0.5rem', fontFamily: 'var(--font-heading)' }}>{job.title}</h3>
                      <div style={{ display: 'flex', gap: '1.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <MapPin size={14} />
                          {job.location}
                        </span>
                        {job.salary && (
                          <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                            <DollarSign size={14} />
                            {job.salary.toLocaleString()}/yr
                          </span>
                        )}
                      </div>
                    </div>
                    <div>
                      <button className="btn btn-secondary" onClick={() => setSelectedJob(job)}>
                        <span>View Details</span>
                        <ChevronRight size={16} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Selected Job Details Page */}
        {view === 'jobs' && selectedJob && (
          <div className="glass-panel" style={{ padding: '2.5rem', maxWidth: '850px', margin: '0 auto' }}>
            <button className="btn btn-secondary btn-sm" onClick={() => setSelectedJob(null)} style={{ marginBottom: '2rem' }}>
              ← Back to Listings
            </button>
            
            <div style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '1.5rem', marginBottom: '2rem' }}>
              <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', marginBottom: '0.75rem' }}>
                <span style={{ color: '#c084fc', fontWeight: 'bold' }}>{selectedJob.company}</span>
                {selectedJob.remote && <span className="status-badge status-shortlisted">Remote</span>}
                <span className="status-badge status-interviewing">{selectedJob.jobType.replace('_', ' ')}</span>
              </div>
              <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem', fontFamily: 'var(--font-heading)' }}>{selectedJob.title}</h2>
              <div style={{ display: 'flex', gap: '2rem', color: 'var(--text-secondary)' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <MapPin size={16} />
                  {selectedJob.location}
                </span>
                {selectedJob.salary && (
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <DollarSign size={16} />
                    ${selectedJob.salary.toLocaleString()} annually
                  </span>
                )}
              </div>
            </div>

            <div style={{ marginBottom: '2rem' }}>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '0.75rem' }}>Job Description</h3>
              <p style={{ color: 'var(--text-secondary)', whiteSpace: 'pre-wrap' }}>{selectedJob.description}</p>
            </div>

            {selectedJob.requirements && (
              <div style={{ marginBottom: '2.5rem' }}>
                <h3 style={{ fontSize: '1.2rem', marginBottom: '0.75rem' }}>Requirements</h3>
                <p style={{ color: 'var(--text-secondary)', whiteSpace: 'pre-wrap' }}>{selectedJob.requirements}</p>
              </div>
            )}

            <div>
              {user?.role === 'ROLE_APPLICANT' ? (
                <button className="btn btn-primary" onClick={() => {
                  fetchMyResumes()
                  setShowApplyModal(selectedJob)
                }}>
                  Apply for this position
                </button>
              ) : user ? (
                <div className="status-badge status-pending" style={{ padding: '0.75rem 1.5rem', fontSize: '0.9rem' }}>
                  Logged in as {user.role.replace('ROLE_', '')}. Applicants only can apply.
                </div>
              ) : (
                <button className="btn btn-primary" onClick={() => setShowAuthModal('login')}>
                  Log In to Apply
                </button>
              )}
            </div>
          </div>
        )}

        {/* APPLICANT: MY APPLICATIONS */}
        {view === 'my-applications' && user?.role === 'ROLE_APPLICANT' && (
          <div>
            <h2 style={{ fontSize: '1.75rem', marginBottom: '1.5rem', fontFamily: 'var(--font-heading)' }}>My Job Applications</h2>
            {myApplications.length === 0 ? (
              <div className="glass-panel" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
                <FileCode size={36} style={{ color: 'var(--text-muted)', marginBottom: '1rem' }} />
                <h3>No Applications Yet</h3>
                <p style={{ color: 'var(--text-secondary)' }}>You haven't applied for any jobs. Explore and apply to active job listings.</p>
                <button className="btn btn-primary" onClick={() => setView('jobs')} style={{ marginTop: '1.5rem' }}>
                  Browse Jobs
                </button>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1.25rem' }}>
                {myApplications.map(app => (
                  <div key={app.id} className="glass-panel" style={{ padding: '1.75rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', marginBottom: '1rem' }}>
                      <div>
                        <h3 style={{ fontSize: '1.25rem', fontFamily: 'var(--font-heading)' }}>{app.jobTitle}</h3>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{app.companyName} · Applied on {new Date(app.createdAt).toLocaleDateString()}</p>
                      </div>
                      <span className={`status-badge status-${app.status.toLowerCase().replace('_', '')}`}>
                        {app.status.replace('_', ' ')}
                      </span>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                      {app.proposedSalary && (
                        <div>
                          <strong>Proposed Salary:</strong> ${app.proposedSalary.toLocaleString()}
                        </div>
                      )}
                      {app.resumeName && (
                        <div>
                          <strong>Attached Resume:</strong> <span style={{ color: 'var(--text-primary)' }}>{app.resumeName}</span>
                        </div>
                      )}
                    </div>

                    {app.coverLetter && (
                      <div style={{ marginTop: '1rem', background: 'rgba(255,255,255,0.02)', padding: '1rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}>
                        <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.25rem', fontWeight: 'bold' }}>Cover Letter:</div>
                        <p style={{ fontSize: '0.9rem', color: 'var(--text-primary)', whiteSpace: 'pre-wrap' }}>{app.coverLetter}</p>
                      </div>
                    )}

                    {app.feedback && (
                      <div style={{ marginTop: '1rem', background: 'rgba(139, 92, 246, 0.05)', padding: '1rem', borderRadius: 'var(--radius-sm)', border: '1px solid rgba(139, 92, 246, 0.2)' }}>
                        <div style={{ fontSize: '0.85rem', color: '#c084fc', marginBottom: '0.25rem', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <Info size={14} /> Recruiter Feedback:
                        </div>
                        <p style={{ fontSize: '0.9rem', color: 'var(--text-primary)', whiteSpace: 'pre-wrap' }}>{app.feedback}</p>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* APPLICANT: RESUMES MANAGEMENT */}
        {view === 'resumes' && user?.role === 'ROLE_APPLICANT' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', flexWrap: 'wrap' }}>
            {/* Upload Area */}
            <div className="glass-panel" style={{ padding: '2rem' }}>
              <h3 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', fontFamily: 'var(--font-heading)' }}>Upload New Resume</h3>
              <form onSubmit={handleUploadResume}>
                <div style={{ marginBottom: '1.5rem' }}>
                  <label>Select Document (PDF, DOC, DOCX - Max 5MB)</label>
                  <input 
                    type="file" 
                    accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document" 
                    onChange={e => setResumeFile(e.target.files[0])}
                    style={{ padding: '0.5rem' }}
                  />
                  {resumeFile && (
                    <div style={{ marginTop: '0.5rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                      Selected: {resumeFile.name} ({(resumeFile.size / (1024 * 1024)).toFixed(2)} MB)
                    </div>
                  )}
                </div>

                <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={isUploadingResume}>
                  {isUploadingResume ? (
                    <>
                      <RefreshCw size={16} className="animate-spin" />
                      Uploading document...
                    </>
                  ) : (
                    <>
                      <Upload size={16} />
                      Upload Resume
                    </>
                  )}
                </button>
              </form>
            </div>

            {/* List of resumes */}
            <div className="glass-panel" style={{ padding: '2rem' }}>
              <h3 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', fontFamily: 'var(--font-heading)' }}>My Resumes</h3>
              {myResumes.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-secondary)' }}>
                  <FileText size={32} style={{ color: 'var(--text-muted)', marginBottom: '0.5rem' }} />
                  <p>No resumes uploaded yet. Upload one on the left to start applying.</p>
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                  {myResumes.map(res => (
                    <div key={res.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <FileText size={20} style={{ color: '#c084fc' }} />
                        <div>
                          <div style={{ fontWeight: '500', fontSize: '0.95rem' }}>{res.fileName}</div>
                          <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                            Uploaded on {new Date(res.uploadedAt).toLocaleDateString()}
                          </div>
                        </div>
                      </div>
                      <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                        {res.primary ? (
                          <span className="status-badge status-accepted" style={{ fontSize: '0.7rem' }}>Primary</span>
                        ) : (
                          <button className="btn btn-secondary btn-sm" style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }} onClick={() => handleSetPrimaryResume(res.id)}>
                            Set Primary
                          </button>
                        )}
                        <button className="btn btn-secondary btn-sm" style={{ padding: '0.25rem', borderRadius: '4px' }} onClick={() => handleDownloadResume(res.id, res.fileName)} title="Download resume">
                          <Upload size={14} style={{ transform: 'rotate(180deg)' }} />
                        </button>
                        <button className="btn btn-danger btn-sm" style={{ padding: '0.25rem', borderRadius: '4px' }} onClick={() => handleDeleteResume(res.id)}>
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {/* RECRUITER: JOBS & APPLICANTS DASHBOARD */}
        {view === 'posted-jobs' && user?.role === 'ROLE_RECRUITER' && (
          <div>
            {!selectedJobForApplicants ? (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                  <h2 style={{ fontSize: '1.75rem', fontFamily: 'var(--font-heading)' }}>My Posted Job Listings</h2>
                  <button className="btn btn-primary" onClick={() => setShowPostJobModal(true)}>
                    <PlusCircle size={16} />
                    Post New Job
                  </button>
                </div>

                {myPostedJobs.length === 0 ? (
                  <div className="glass-panel" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
                    <Building size={36} style={{ color: 'var(--text-muted)', marginBottom: '1rem' }} />
                    <h3>No Job Listings Posted</h3>
                    <p style={{ color: 'var(--text-secondary)' }}>You haven't posted any job listings. Click 'Post New Job' to recruit talents.</p>
                  </div>
                ) : (
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1.25rem' }}>
                    {myPostedJobs.map(job => (
                      <div key={job.id} className="glass-panel" style={{ padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1.5rem' }}>
                        <div>
                          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '0.5rem' }}>
                            <span className={`status-badge status-${job.status.toLowerCase()}`}>
                              {job.status}
                            </span>
                            <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{job.jobType.replace('_', ' ')}</span>
                            {job.remote && <span className="status-badge status-shortlisted" style={{ fontSize: '0.7rem' }}>Remote</span>}
                          </div>
                          <h3 style={{ fontSize: '1.25rem', fontFamily: 'var(--font-heading)', marginBottom: '0.25rem' }}>{job.title}</h3>
                          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Posted on {new Date(job.createdAt).toLocaleDateString()} · {job.location}</p>
                        </div>
                        <div style={{ display: 'flex', gap: '0.75rem' }}>
                          <button className="btn btn-secondary btn-sm" onClick={() => {
                            setSelectedJobForApplicants(job)
                            fetchJobApplicants(job.id)
                          }}>
                            <Users size={16} />
                            <span>View Applicants</span>
                          </button>
                          <button 
                            className={`btn btn-sm ${job.status === 'ACTIVE' ? 'btn-secondary' : 'btn-primary'}`}
                            onClick={() => handleToggleJobStatus(job.id, job.status)}
                          >
                            <span>{job.status === 'ACTIVE' ? 'Close Listing' : 'Activate'}</span>
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ) : (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                  <button className="btn btn-secondary btn-sm" onClick={() => setSelectedJobForApplicants(null)}>
                    ← Back to Job Postings
                  </button>
                  <h3 style={{ fontSize: '1.35rem', fontFamily: 'var(--font-heading)' }}>
                    Applicants for: <span style={{ color: '#c084fc' }}>{selectedJobForApplicants.title}</span>
                  </h3>
                </div>

                {selectedJobApplicants.length === 0 ? (
                  <div className="glass-panel" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
                    <Users size={36} style={{ color: 'var(--text-muted)', marginBottom: '1rem' }} />
                    <h3>No Applicants Yet</h3>
                    <p style={{ color: 'var(--text-secondary)' }}>No applicants have applied to this job listing yet. Monitor this workspace for future applications.</p>
                  </div>
                ) : (
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1.5rem' }}>
                    {selectedJobApplicants.map(app => (
                      <div key={app.id} className="glass-panel" style={{ padding: '1.75rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', marginBottom: '1rem' }}>
                          <div>
                            <h4 style={{ fontSize: '1.15rem' }}>{app.applicantName}</h4>
                            <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>Email: {app.applicantEmail} · Phone: {app.applicantPhone || 'N/A'}</p>
                          </div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                            <span className={`status-badge status-${app.status.toLowerCase().replace('_', '')}`}>
                              {app.status.replace('_', ' ')}
                            </span>
                            <button className="btn btn-primary btn-sm" onClick={() => {
                              setSelectedApplicationForStatus(app)
                              setNewApplicationStatus(app.status)
                              setStatusFeedback(app.feedback || '')
                            }}>
                              Manage Status
                            </button>
                          </div>
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '1rem' }}>
                          <div>
                            <strong>Salary Expectation:</strong> {app.proposedSalary ? `$${app.proposedSalary.toLocaleString()}` : 'Not Specified'}
                          </div>
                          {app.resumeId && (
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                              <strong>Resume:</strong> 
                              <button 
                                className="btn btn-secondary btn-sm" 
                                style={{ padding: '0.2rem 0.5rem', fontSize: '0.75rem' }}
                                onClick={() => handleDownloadResume(app.resumeId, app.resumeName || `${app.applicantName}_resume.pdf`)}
                              >
                                <Upload size={12} style={{ transform: 'rotate(180deg)' }} />
                                <span>{app.resumeName || 'Download Resume'}</span>
                              </button>
                            </div>
                          )}
                        </div>

                        {app.coverLetter && (
                          <div style={{ background: 'rgba(255,255,255,0.015)', padding: '0.75rem 1rem', borderRadius: '4px', border: '1px solid var(--border-color)', fontSize: '0.9rem', marginBottom: '1rem' }}>
                            <strong>Cover Letter:</strong>
                            <p style={{ marginTop: '0.25rem', whiteSpace: 'pre-wrap', color: 'var(--text-primary)' }}>{app.coverLetter}</p>
                          </div>
                        )}

                        {app.feedback && (
                          <div style={{ background: 'rgba(139, 92, 246, 0.03)', padding: '0.75rem 1rem', borderRadius: '4px', border: '1px solid rgba(139, 92, 246, 0.15)', fontSize: '0.9rem' }}>
                            <strong>Your Comments:</strong>
                            <p style={{ marginTop: '0.25rem', color: 'var(--text-secondary)' }}>{app.feedback}</p>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* ADMIN: REPORTS / STATISTICS */}
        {view === 'admin-reports' && user?.role === 'ROLE_ADMIN' && (
          <div>
            <h2 style={{ fontSize: '1.75rem', marginBottom: '2rem', fontFamily: 'var(--font-heading)' }}>Platform Operations Dashboard</h2>
            
            {adminReports ? (
              <div>
                {/* Scorecards */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(230px, 1fr))', gap: '1.5rem', marginBottom: '3rem' }}>
                  <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <div style={{ background: 'rgba(139, 92, 246, 0.12)', padding: '0.75rem', borderRadius: '12px' }}>
                      <Users size={24} style={{ color: '#c084fc' }} />
                    </div>
                    <div>
                      <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', fontWeight: '500' }}>TOTAL USERS</div>
                      <div style={{ fontSize: '1.75rem', fontWeight: 'bold' }}>{adminReports.totalUsers}</div>
                    </div>
                  </div>

                  <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <div style={{ background: 'rgba(59, 130, 246, 0.12)', padding: '0.75rem', borderRadius: '12px' }}>
                      <Briefcase size={24} style={{ color: '#60a5fa' }} />
                    </div>
                    <div>
                      <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', fontWeight: '500' }}>ACTIVE JOBS</div>
                      <div style={{ fontSize: '1.75rem', fontWeight: 'bold' }}>{adminReports.totalActiveJobs}</div>
                    </div>
                  </div>

                  <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <div style={{ background: 'rgba(16, 185, 129, 0.12)', padding: '0.75rem', borderRadius: '12px' }}>
                      <FileSignature size={24} style={{ color: '#34d399' }} />
                    </div>
                    <div>
                      <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', fontWeight: '500' }}>APPLICATIONS</div>
                      <div style={{ fontSize: '1.75rem', fontWeight: 'bold' }}>{adminReports.totalApplications}</div>
                    </div>
                  </div>

                  <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <div style={{ background: 'rgba(245, 158, 11, 0.12)', padding: '0.75rem', borderRadius: '12px' }}>
                      <FileText size={24} style={{ color: '#fbbf24' }} />
                    </div>
                    <div>
                      <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', fontWeight: '500' }}>RESUMES UPLOADED</div>
                      <div style={{ fontSize: '1.75rem', fontWeight: 'bold' }}>{adminReports.totalResumes}</div>
                    </div>
                  </div>
                </div>

                {/* Sub-breakdown sections */}
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', flexWrap: 'wrap' }}>
                  <div className="glass-panel" style={{ padding: '2rem' }}>
                    <h3 style={{ fontSize: '1.2rem', marginBottom: '1.25rem', fontFamily: 'var(--font-heading)' }}>User Role Breakdown</h3>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Applicants</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.applicantsCount}</strong>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Recruiters</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.recruitersCount}</strong>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Administrators</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.adminsCount}</strong>
                      </div>
                    </div>
                  </div>

                  <div className="glass-panel" style={{ padding: '2rem' }}>
                    <h3 style={{ fontSize: '1.2rem', marginBottom: '1.25rem', fontFamily: 'var(--font-heading)' }}>Hiring Funnel Status</h3>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Pending Applications</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.applicationsByStatus?.PENDING || 0}</strong>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Shortlisted</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.applicationsByStatus?.SHORTLISTED || 0}</strong>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Interview Scheduled</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.applicationsByStatus?.INTERVIEW_SCHEDULED || 0}</strong>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span style={{ color: 'var(--text-secondary)' }}>Offered Positions</span>
                        <strong style={{ color: 'var(--text-primary)' }}>{adminReports.applicationsByStatus?.OFFERED || 0}</strong>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '3rem 0' }}>
                <RefreshCw size={28} className="animate-spin" style={{ color: 'var(--accent-primary)', margin: '0 auto' }} />
              </div>
            )}
          </div>
        )}

        {/* ADMIN: USER MANAGEMENT */}
        {view === 'admin-users' && user?.role === 'ROLE_ADMIN' && (
          <div>
            <h2 style={{ fontSize: '1.75rem', marginBottom: '1.5rem', fontFamily: 'var(--font-heading)' }}>Platform User Management</h2>
            
            {/* Search filter */}
            <div className="glass-panel" style={{ padding: '1rem 1.5rem', display: 'flex', gap: '1rem', marginBottom: '2rem', alignItems: 'center' }}>
              <Search size={18} style={{ color: 'var(--text-muted)' }} />
              <input 
                type="text" 
                placeholder="Search user name or email address..." 
                value={adminSearchUser}
                onChange={e => {
                  setAdminSearchUser(e.target.value)
                  fetchAdminUsers(e.target.value)
                }}
              />
            </div>

            {/* User Directory list */}
            {adminUsers.length === 0 ? (
              <div className="glass-panel" style={{ padding: '3rem 2rem', textAlign: 'center' }}>
                <p style={{ color: 'var(--text-secondary)' }}>No registered users found matching the search criteria.</p>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1rem' }}>
                {adminUsers.map(u => (
                  <div key={u.id} className="glass-panel" style={{ padding: '1.25rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                    <div>
                      <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '0.25rem' }}>
                        <strong style={{ fontSize: '1.1rem' }}>{u.firstName} {u.lastName}</strong>
                        <span className="user-badge" style={{ padding: '0.15rem 0.5rem', fontSize: '0.7rem' }}>{u.roles[0].replace('ROLE_', '')}</span>
                      </div>
                      <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Email: {u.email} · Phone: {u.phone || 'N/A'}</span>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      <span style={{ fontSize: '0.85rem', color: u.enabled ? 'var(--color-success)' : 'var(--color-error)' }}>
                        {u.enabled ? 'Active Account' : 'Account Suspended'}
                      </span>
                      <button 
                        className={`btn btn-sm ${u.enabled ? 'btn-danger' : 'btn-primary'}`} 
                        onClick={() => handleToggleUserEnable(u.id)}
                        disabled={u.email === 'admin@jobportal.com'}
                      >
                        {u.enabled ? <UserX size={14} /> : <UserCheck size={14} />}
                        <span>{u.enabled ? 'Suspend' : 'Unsuspend'}</span>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

      </main>

      {/* Footer */}
      <footer style={{ borderTop: '1px solid var(--border-color)', padding: '2rem 1.5rem', textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '4rem', background: 'rgba(11, 11, 15, 0.8)' }}>
        <p>© {new Date().getFullYear()} CareerPulse System. Build a robust job marketplace. Hardened & Secured.</p>
      </footer>

      {/* AUTH MODALS: LOGIN & REGISTER */}
      {showAuthModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel">
            <button className="modal-close" onClick={() => setShowAuthModal(null)}>
              <X size={18} />
            </button>
            
            <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem', fontFamily: 'var(--font-heading)', textAlign: 'center' }}>
              {showAuthModal === 'login' ? 'Welcome Back' : 'Create Account'}
            </h2>

            {showAuthModal === 'login' ? (
              <form onSubmit={handleLogin}>
                <div style={{ marginBottom: '1rem' }}>
                  <label>Email Address</label>
                  <input type="email" placeholder="you@example.com" required value={authEmail} onChange={e => setAuthEmail(e.target.value)} />
                </div>
                <div style={{ marginBottom: '1.5rem' }}>
                  <label>Password</label>
                  <input type="password" placeholder="••••••••" required value={authPassword} onChange={e => setAuthPassword(e.target.value)} />
                </div>
                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Sign In</button>
                <p style={{ marginTop: '1rem', textAlign: 'center', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                  New to CareerPulse?{' '}
                  <span style={{ color: 'var(--accent-primary)', cursor: 'pointer', fontWeight: '500' }} onClick={() => setShowAuthModal('register')}>
                    Create an account
                  </span>
                </p>
              </form>
            ) : (
              <form onSubmit={handleRegister}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                  <div>
                    <label>First Name</label>
                    <input type="text" placeholder="John" required value={authFirstName} onChange={e => setAuthFirstName(e.target.value)} />
                  </div>
                  <div>
                    <label>Last Name</label>
                    <input type="text" placeholder="Doe" required value={authLastName} onChange={e => setAuthLastName(e.target.value)} />
                  </div>
                </div>
                <div style={{ marginBottom: '1rem' }}>
                  <label>Email Address</label>
                  <input type="email" placeholder="name@domain.com" required value={authEmail} onChange={e => setAuthEmail(e.target.value)} />
                </div>
                <div style={{ marginBottom: '1rem' }}>
                  <label>Password</label>
                  <input type="password" placeholder="Min. 8 characters" required value={authPassword} onChange={e => setAuthPassword(e.target.value)} />
                </div>
                <div style={{ marginBottom: '1rem' }}>
                  <label>Phone Number</label>
                  <input type="tel" placeholder="+91 9999999999" value={authPhone} onChange={e => setAuthPhone(e.target.value)} />
                </div>
                <div style={{ marginBottom: '1.5rem' }}>
                  <label>Register As</label>
                  <select value={authRole} onChange={e => setAuthRole(e.target.value)}>
                    <option value="ROLE_APPLICANT">Applicant (looking for jobs)</option>
                    <option value="ROLE_RECRUITER">Recruiter (posting jobs)</option>
                  </select>
                </div>
                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Create Account</button>
                <p style={{ marginTop: '1rem', textAlign: 'center', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                  Already have an account?{' '}
                  <span style={{ color: 'var(--accent-primary)', cursor: 'pointer', fontWeight: '500' }} onClick={() => setShowAuthModal('login')}>
                    Sign In instead
                  </span>
                </p>
              </form>
            )}
          </div>
        </div>
      )}

      {/* APPLICANT APPLY MODAL */}
      {showApplyModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel" style={{ maxWidth: '600px' }}>
            <button className="modal-close" onClick={() => setShowApplyModal(null)}>
              <X size={18} />
            </button>
            <h2 style={{ fontSize: '1.5rem', marginBottom: '0.5rem', fontFamily: 'var(--font-heading)' }}>Apply for Job</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
              Applying to: <strong>{showApplyModal.title}</strong> at <strong>{showApplyModal.company}</strong>
            </p>

            <form onSubmit={handleApplySubmit}>
              <div style={{ marginBottom: '1rem' }}>
                <label>Select Cover Resume</label>
                {myResumes.length === 0 ? (
                  <div style={{ padding: '1rem', background: 'rgba(239, 68, 68, 0.08)', border: '1px solid rgba(239, 68, 68, 0.2)', borderRadius: '4px', fontSize: '0.85rem' }}>
                    No resumes found. Please go to the <strong>Resumes</strong> tab and upload at least one resume document (.pdf/.doc/.docx) before applying.
                  </div>
                ) : (
                  <select value={selectedResumeId} onChange={e => setSelectedResumeId(e.target.value)} required>
                    {myResumes.map(r => (
                      <option key={r.id} value={r.id}>{r.fileName} {r.primary ? '(Primary)' : ''}</option>
                    ))}
                  </select>
                )}
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label>Cover Letter (Optional)</label>
                <textarea 
                  rows="4" 
                  placeholder="Explain why you are a good fit for this role..." 
                  value={coverLetter} 
                  onChange={e => setCoverLetter(e.target.value)}
                />
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label>Expected Salary ($ / yr) - Optional</label>
                <input 
                  type="number" 
                  placeholder="e.g. 75000" 
                  value={proposedSalary} 
                  onChange={e => setProposedSalary(e.target.value)} 
                />
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowApplyModal(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={myResumes.length === 0}>Submit Application</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* RECRUITER POST JOB MODAL */}
      {showPostJobModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel" style={{ maxWidth: '650px' }}>
            <button className="modal-close" onClick={() => setShowPostJobModal(false)}>
              <X size={18} />
            </button>
            <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem', fontFamily: 'var(--font-heading)', textAlign: 'center' }}>Post New Job Listing</h2>
            
            <form onSubmit={handlePostJob}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <label>Job Title</label>
                  <input type="text" placeholder="Senior Java Developer" required value={jobPostTitle} onChange={e => setJobPostTitle(e.target.value)} />
                </div>
                <div>
                  <label>Company Name</label>
                  <input type="text" placeholder="InnovateTech Inc." required value={jobPostCompany} onChange={e => setJobPostCompany(e.target.value)} />
                </div>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label>Job Description</label>
                <textarea rows="4" placeholder="Detail the duties, responsibilities, and team workflow..." required value={jobPostDesc} onChange={e => setJobPostDesc(e.target.value)} />
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label>Skills & Requirements</label>
                <textarea rows="3" placeholder="List key qualifications, stack, and experience required..." value={jobPostReqs} onChange={e => setJobPostReqs(e.target.value)} />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <label>Location</label>
                  <input type="text" placeholder="San Francisco, CA" required value={jobPostLoc} onChange={e => setJobPostLoc(e.target.value)} />
                </div>
                <div>
                  <label>Job Type</label>
                  <select value={jobPostType} onChange={e => setJobPostType(e.target.value)}>
                    <option value="FULL_TIME">Full Time</option>
                    <option value="PART_TIME">Part Time</option>
                    <option value="CONTRACT">Contract</option>
                    <option value="INTERNSHIP">Internship</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 120px', gap: '1rem', alignItems: 'center', marginBottom: '1.5rem' }}>
                <div>
                  <label>Annual Salary ($) - Optional</label>
                  <input type="number" placeholder="e.g. 120000" value={jobPostSalary} onChange={e => setJobPostSalary(e.target.value)} />
                </div>
                <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', marginTop: '1.75rem' }}>
                  <input 
                    type="checkbox" 
                    checked={jobPostRemote} 
                    onChange={e => setJobPostRemote(e.target.checked)} 
                    style={{ width: 'auto', transform: 'scale(1.2)' }} 
                  />
                  <span>Remote</span>
                </label>
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowPostJobModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Post Listing</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* RECRUITER MANAGE STATUS MODAL */}
      {selectedApplicationForStatus && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel" style={{ maxWidth: '500px' }}>
            <button className="modal-close" onClick={() => setSelectedApplicationForStatus(null)}>
              <X size={18} />
            </button>
            <h2 style={{ fontSize: '1.5rem', marginBottom: '0.5rem', fontFamily: 'var(--font-heading)' }}>Update Hiring Status</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
              Applicant: <strong>{selectedApplicationForStatus.applicantName}</strong>
            </p>

            <form onSubmit={handleUpdateHiringStatus}>
              <div style={{ marginBottom: '1rem' }}>
                <label>Hiring Status</label>
                <select value={newApplicationStatus} onChange={e => setNewApplicationStatus(e.target.value)}>
                  <option value="UNDER_REVIEW">UNDER REVIEW</option>
                  <option value="SHORTLISTED">SHORTLISTED</option>
                  <option value="INTERVIEW_SCHEDULED">INTERVIEW SCHEDULED</option>
                  <option value="OFFERED">OFFERED</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label>Feedback & Comments</label>
                <textarea 
                  rows="4" 
                  placeholder="Provide interview details, offer salary, or reasons for rejection..." 
                  value={statusFeedback} 
                  onChange={e => setStatusFeedback(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setSelectedApplicationForStatus(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
