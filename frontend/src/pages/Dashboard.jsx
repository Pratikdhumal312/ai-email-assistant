import React, { useState, useEffect, useMemo } from 'react';
import { Search, Mail, Clock, AlertTriangle, CheckCircle, Send, Edit3, Filter, BarChart3, Users, MessageSquare, RefreshCw } from 'lucide-react';

const Dashboard = () => {
  const [emails, setEmails] = useState([]);
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [filterSentiment, setFilterSentiment] = useState('all');
  const [filterPriority, setFilterPriority] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [generatedResponse, setGeneratedResponse] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [analytics, setAnalytics] = useState({
    total: 0,
    urgent: 0,
    resolved: 0,
    pending: 0,
    sentimentCounts: { positive: 0, negative: 0, neutral: 0 }
  });

  // API Base URL - Update this to match your Spring Boot server
  const API_BASE_URL = 'http://localhost:8080/api';

  // Fetch emails from Spring Boot backend
  const fetchEmails = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_BASE_URL}/emails`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setEmails(data);
    } catch (err) {
      console.error('Error fetching emails:', err);
      setError('Failed to load emails. Please check your backend connection.');
      // Fallback to demo data for development
      setEmails(getDemoData());
    } finally {
      setLoading(false);
    }
  };

  // Fetch analytics from Spring Boot backend
  const fetchAnalytics = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/analytics`);
      if (response.ok) {
        const data = await response.json();
        setAnalytics(data);
      }
    } catch (err) {
      console.error('Error fetching analytics:', err);
    }
  };

  // Generate AI response for selected email
  const generateAIResponse = async (emailId) => {
    if (!emailId) return;
    
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/emails/${emailId}/generate-response`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setGeneratedResponse(data.response || '');
    } catch (err) {
      console.error('Error generating response:', err);
      setError('Failed to generate AI response');
      // Fallback to template response
      setGeneratedResponse(getTemplateResponse(selectedEmail));
    } finally {
      setLoading(false);
    }
  };

  // Send response via Spring Boot backend
  const sendResponse = async () => {
    if (!selectedEmail || !generatedResponse.trim()) {
      setError('Please select an email and ensure response is generated');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/emails/${selectedEmail.id}/respond`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          response: generatedResponse,
          emailId: selectedEmail.id
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      
      // Update email status locally
      setEmails(prev => prev.map(email => 
        email.id === selectedEmail.id 
          ? { ...email, status: 'RESOLVED' }
          : email
      ));
      
      setSelectedEmail(null);
      setGeneratedResponse('');
      setError('');
      alert('Response sent successfully!');
      
      // Refresh analytics
      fetchAnalytics();
    } catch (err) {
      console.error('Error sending response:', err);
      setError('Failed to send response. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Demo data for development/fallback
  const getDemoData = () => [
    {
      id: 1,
      sender: 'eve@startup.io',
      subject: 'Help required with account verification',
      body: 'Do you support integration with third-party APIs? Specifically, I\'m looking for CRM integration options.',
      sentDate: '2025-08-19T00:58:00',
      sentiment: 'NEUTRAL',
      priority: 'NORMAL',
      category: 'INTEGRATION',
      status: 'PENDING',
      extractedInfo: {
        contactDetails: 'eve@startup.io',
        requirements: 'CRM integration support',
        keywords: ['API', 'integration', 'CRM']
      }
    },
    {
      id: 2,
      sender: 'diana@client.co',
      subject: 'General query about subscription',
      body: 'Hi team, I am unable to log into my account since yesterday. Could you please help me resolve this issue?',
      sentDate: '2025-08-25T00:58:00',
      sentiment: 'NEGATIVE',
      priority: 'URGENT',
      category: 'LOGIN',
      status: 'PENDING',
      extractedInfo: {
        contactDetails: 'diana@client.co',
        requirements: 'Account access restoration',
        keywords: ['login', 'account', 'unable', 'help']
      }
    },
    {
      id: 3,
      sender: 'alice@example.com',
      subject: 'Critical help needed for downtime',
      body: 'Our servers are down, and we need immediate support. This is highly critical.',
      sentDate: '2025-08-26T02:58:00',
      sentiment: 'NEGATIVE',
      priority: 'URGENT',
      category: 'TECHNICAL',
      status: 'PENDING',
      extractedInfo: {
        contactDetails: 'alice@example.com',
        requirements: 'Server downtime resolution',
        keywords: ['servers', 'down', 'immediate', 'critical']
      }
    }
  ];

  // Template response fallback
  const getTemplateResponse = (email) => {
    if (!email) return '';
    
    const templates = {
      INTEGRATION: `Dear ${email.sender.split('@')[0]},\n\nThank you for reaching out regarding integration options. We do support third-party API integrations including popular CRM platforms.\n\nI'd be happy to schedule a technical consultation to discuss your specific requirements.\n\nBest regards,\nSupport Team`,
      LOGIN: `Dear ${email.sender.split('@')[0]},\n\nI understand your frustration with the login issue, and I sincerely apologize for the inconvenience.\n\nI've escalated your case to our technical team and will provide updates within 2 hours.\n\nBest regards,\nSupport Team`,
      TECHNICAL: `Dear ${email.sender.split('@')[0]},\n\nWe understand this is critical for your operations and are treating this with highest priority.\n\nOur engineering team has been notified and is actively investigating.\n\nBest regards,\nSupport Team`,
      BILLING: `Dear ${email.sender.split('@')[0]},\n\nI sincerely apologize for the billing issue. I've processed a refund and you should see it reflected within 2-3 business days.\n\nBest regards,\nSupport Team`
    };
    
    return templates[email.category] || 'Thank you for contacting us. We will review your request and respond shortly.';
  };

  // Initialize data on component mount
  useEffect(() => {
    fetchEmails();
    fetchAnalytics();
  }, []);

  // Calculate analytics from emails if backend doesn't provide them
  useEffect(() => {
    if (emails.length > 0) {
      const total = emails.length;
      const urgent = emails.filter(e => e.priority === 'URGENT').length;
      const resolved = emails.filter(e => e.status === 'RESOLVED').length;
      const pending = emails.filter(e => e.status === 'PENDING').length;
      const sentimentCounts = {
        positive: emails.filter(e => e.sentiment === 'POSITIVE').length,
        negative: emails.filter(e => e.sentiment === 'NEGATIVE').length,
        neutral: emails.filter(e => e.sentiment === 'NEUTRAL').length
      };

      setAnalytics({ total, urgent, resolved, pending, sentimentCounts });
    }
  }, [emails]);

  // Filter and sort emails
  const filteredEmails = useMemo(() => {
    let filtered = emails.filter(email => {
      const matchesSentiment = filterSentiment === 'all' || email.sentiment === filterSentiment.toUpperCase();
      const matchesPriority = filterPriority === 'all' || 
        (filterPriority === 'urgent' && email.priority === 'URGENT') ||
        (filterPriority === 'normal' && email.priority === 'NORMAL');
      const matchesSearch = email.subject?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           email.sender?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           email.body?.toLowerCase().includes(searchTerm.toLowerCase());
      return matchesSentiment && matchesPriority && matchesSearch;
    });

    // Sort by priority (urgent first) then by date
    return filtered.sort((a, b) => {
      if (a.priority === 'URGENT' && b.priority !== 'URGENT') return -1;
      if (b.priority === 'URGENT' && a.priority !== 'URGENT') return 1;
      return new Date(b.sentDate) - new Date(a.sentDate);
    });
  }, [emails, filterSentiment, filterPriority, searchTerm]);

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const getSentimentColor = (sentiment) => {
    switch (sentiment) {
      case 'POSITIVE': return 'text-green-600 bg-green-50';
      case 'NEGATIVE': return 'text-red-600 bg-red-50';
      default: return 'text-gray-600 bg-gray-50';
    }
  };

  const getPriorityColor = (priority) => {
    return priority === 'URGENT' ? 'text-red-600 bg-red-50' : 'text-blue-600 bg-blue-50';
  };

  const handleEmailSelect = (email) => {
    setSelectedEmail(email);
    setError('');
    generateAIResponse(email.id);
  };

  const refreshData = () => {
    fetchEmails();
    fetchAnalytics();
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">AI-Powered Communication Assistant</h1>
              <p className="text-gray-600">Intelligent email management with Spring Boot backend</p>
            </div>
            <button
              onClick={refreshData}
              disabled={loading}
              className="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
              <span>Refresh</span>
            </button>
          </div>
          
          {error && (
            <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-700 text-sm">{error}</p>
            </div>
          )}
        </div>

        {/* Analytics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <div className="p-2 bg-blue-50 rounded-lg">
                <Mail className="w-6 h-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Total Emails (24h)</p>
                <p className="text-2xl font-bold text-gray-900">{analytics.total}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <div className="p-2 bg-red-50 rounded-lg">
                <AlertTriangle className="w-6 h-6 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Urgent Emails</p>
                <p className="text-2xl font-bold text-red-600">{analytics.urgent}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <div className="p-2 bg-green-50 rounded-lg">
                <CheckCircle className="w-6 h-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Resolved</p>
                <p className="text-2xl font-bold text-green-600">{analytics.resolved}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <div className="p-2 bg-yellow-50 rounded-lg">
                <Clock className="w-4 h-6 text-yellow-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Pending</p>
                <p className="text-2xl font-bold text-yellow-600">{analytics.pending}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Filters and Search */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search emails..."
                className="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            <select
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              value={filterSentiment}
              onChange={(e) => setFilterSentiment(e.target.value)}
            >
              <option value="all">All Sentiments</option>
              <option value="positive">Positive</option>
              <option value="negative">Negative</option>
              <option value="neutral">Neutral</option>
            </select>

            <select
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              value={filterPriority}
              onChange={(e) => setFilterPriority(e.target.value)}
            >
              <option value="all">All Priorities</option>
              <option value="urgent">Urgent</option>
              <option value="normal">Normal</option>
            </select>

            <div className="flex items-center space-x-2">
              <Filter className="w-4 h-4 text-gray-500" />
              <span className="text-sm text-gray-600">{filteredEmails.length} emails</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Email List */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow-sm">
              <div className="p-4 border-b border-gray-200">
                <h2 className="text-lg font-semibold text-gray-900">Support Emails</h2>
              </div>
              <div className="max-h-96 overflow-y-auto">
                {loading && emails.length === 0 ? (
                  <div className="p-8 text-center">
                    <RefreshCw className="w-8 h-8 animate-spin mx-auto mb-4 text-blue-600" />
                    <p className="text-gray-600">Loading emails...</p>
                  </div>
                ) : filteredEmails.length === 0 ? (
                  <div className="p-8 text-center">
                    <Mail className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                    <p className="text-gray-600">No emails found matching your criteria</p>
                  </div>
                ) : (
                  filteredEmails.map((email) => (
                    <div
                      key={email.id}
                      className={`p-4 border-b border-gray-100 cursor-pointer hover:bg-gray-50 transition-colors ${
                        selectedEmail?.id === email.id ? 'bg-blue-50 border-blue-200' : ''
                      }`}
                      onClick={() => handleEmailSelect(email)}
                    >
                      <div className="flex items-start justify-between mb-2">
                        <div className="flex-1">
                          <div className="flex items-center space-x-2 mb-1">
                            <h3 className="font-medium text-gray-900 text-sm">{email.subject}</h3>
                            {email.priority === 'URGENT' && (
                              <AlertTriangle className="w-4 h-4 text-red-500" />
                            )}
                          </div>
                          <p className="text-xs text-gray-600">{email.sender}</p>
                        </div>
                        <div className="flex flex-col items-end space-y-1">
                          <span className={`px-2 py-1 text-xs rounded-full ${getSentimentColor(email.sentiment)}`}>
                            {email.sentiment?.toLowerCase()}
                          </span>
                          <span className={`px-2 py-1 text-xs rounded-full ${getPriorityColor(email.priority)}`}>
                            {email.priority?.toLowerCase()}
                          </span>
                        </div>
                      </div>
                      <p className="text-sm text-gray-700 mb-2 line-clamp-2">{email.body}</p>
                      <div className="flex items-center justify-between">
                        <span className="text-xs text-gray-500">{formatDate(email.sentDate)}</span>
                        <span className={`text-xs px-2 py-1 rounded-full ${
                          email.status === 'RESOLVED' ? 'bg-green-50 text-green-600' : 'bg-yellow-50 text-yellow-600'
                        }`}>
                          {email.status?.toLowerCase()}
                        </span>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>

          {/* Email Details and Response */}
          <div className="lg:col-span-1">
            {selectedEmail ? (
              <div className="bg-white rounded-lg shadow-sm">
                <div className="p-4 border-b border-gray-200">
                  <h2 className="text-lg font-semibold text-gray-900">Email Details</h2>
                </div>
                <div className="p-4 space-y-4">
                  <div>
                    <h3 className="text-sm font-medium text-gray-900 mb-1">Sender</h3>
                    <p className="text-sm text-gray-600">{selectedEmail.sender}</p>
                  </div>
                  
                  <div>
                    <h3 className="text-sm font-medium text-gray-900 mb-1">Subject</h3>
                    <p className="text-sm text-gray-600">{selectedEmail.subject}</p>
                  </div>
                  
                  <div>
                    <h3 className="text-sm font-medium text-gray-900 mb-1">Message</h3>
                    <p className="text-sm text-gray-600">{selectedEmail.body}</p>
                  </div>

                  {selectedEmail.extractedInfo && (
                    <div>
                      <h3 className="text-sm font-medium text-gray-900 mb-1">Extracted Information</h3>
                      <div className="bg-gray-50 p-3 rounded-lg text-sm">
                        <p><strong>Requirements:</strong> {selectedEmail.extractedInfo.requirements}</p>
                        {selectedEmail.extractedInfo.keywords && (
                          <p><strong>Keywords:</strong> {selectedEmail.extractedInfo.keywords.join(', ')}</p>
                        )}
                      </div>
                    </div>
                  )}

                  <div>
                    <h3 className="text-sm font-medium text-gray-900 mb-2">
                      AI Generated Response
                      {loading && <span className="text-blue-600 ml-2">(Generating...)</span>}
                    </h3>
                    <textarea
                      className="w-full h-32 p-3 border border-gray-300 rounded-lg text-sm resize-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      value={generatedResponse}
                      onChange={(e) => setGeneratedResponse(e.target.value)}
                      placeholder="AI will generate a response here..."
                      disabled={loading}
                    />
                  </div>

                  <div className="flex space-x-2">
                    <button
                      onClick={sendResponse}
                      disabled={loading || !generatedResponse.trim()}
                      className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors flex items-center justify-center space-x-1"
                    >
                      <Send className="w-4 h-4" />
                      <span>{loading ? 'Sending...' : 'Send Response'}</span>
                    </button>
                    <button 
                      onClick={() => generateAIResponse(selectedEmail.id)}
                      disabled={loading}
                      className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 transition-colors"
                    >
                      <Edit3 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow-sm p-8 text-center">
                <MessageSquare className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">Select an Email</h3>
                <p className="text-gray-600">Choose an email from the list to view details and generate a response.</p>
              </div>
            )}
          </div>
        </div>

        {/* Analytics Chart */}
        <div className="mt-6 bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Sentiment Analysis Overview</h2>
          <div className="grid grid-cols-3 gap-4">
            <div className="text-center">
              <div className="w-full bg-green-100 rounded-full h-2 mb-2">
                <div 
                  className="bg-green-600 h-2 rounded-full" 
                  style={{ width: `${analytics.total ? (analytics.sentimentCounts.positive / analytics.total) * 100 : 0}%` }}
                ></div>
              </div>
              <p className="text-sm text-gray-600">Positive ({analytics.sentimentCounts.positive})</p>
            </div>
            <div className="text-center">
              <div className="w-full bg-red-100 rounded-full h-2 mb-2">
                <div 
                  className="bg-red-600 h-2 rounded-full" 
                  style={{ width: `${analytics.total ? (analytics.sentimentCounts.negative / analytics.total) * 100 : 0}%` }}
                ></div>
              </div>
              <p className="text-sm text-gray-600">Negative ({analytics.sentimentCounts.negative})</p>
            </div>
            <div className="text-center">
              <div className="w-full bg-gray-100 rounded-full h-2 mb-2">
                <div 
                  className="bg-gray-600 h-2 rounded-full" 
                  style={{ width: `${analytics.total ? (analytics.sentimentCounts.neutral / analytics.total) * 100 : 0}%` }}
                ></div>
              </div>
              <p className="text-sm text-gray-600">Neutral ({analytics.sentimentCounts.neutral})</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;