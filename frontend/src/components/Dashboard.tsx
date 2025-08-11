import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Plus, 
  Search, 
  Filter, 
  Edit, 
  Trash2, 
  Calendar, 
  User, 
  LogOut,
  BookOpen,
  Smile,
  Frown,
  Meh,
  TrendingUp,
  BarChart3,
  Settings,
  Bell
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { journalAPI, userAPI } from '../services/api';
import { JournalEntry } from '../types';
import JournalEntryModal from './JournalEntryModal';

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [entries, setEntries] = useState<JournalEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterSentiment, setFilterSentiment] = useState<string>('all');
  const [showModal, setShowModal] = useState(false);
  const [editingEntry, setEditingEntry] = useState<JournalEntry | null>(null);
  const [greeting, setGreeting] = useState('');

  useEffect(() => {
    fetchEntries();
    fetchGreeting();
  }, []);

  const fetchEntries = async () => {
    try {
      const response = await journalAPI.getAllEntries();
      setEntries(response.data || []);
    } catch (error) {
      console.error('Error fetching entries:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchGreeting = async () => {
    try {
      const response = await userAPI.getProfile();
      setGreeting(response.data);
    } catch (error) {
      console.error('Error fetching greeting:', error);
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this entry? This action cannot be undone.')) {
      try {
        console.log('=== FRONTEND DELETE START ===');
        console.log('Attempting to delete entry with id:', id);
        console.log('ID type:', typeof id);
        console.log('ID length:', id.length);
        console.log('Current entries before delete:', entries);
        console.log('Entry to delete:', entries.find(e => e.id === id));
        
        // Check if the ID looks like a valid MongoDB ObjectId
        if (!id || id.length !== 24) {
          console.error('Invalid ID format:', id);
          alert('Invalid entry ID format');
          return;
        }
        
        console.log('Making DELETE request to:', `/journal/id/${id}`);
        const response = await journalAPI.deleteEntry(id);
        console.log('Delete response:', response);
        console.log('Response status:', response.status);
        
        const updatedEntries = entries.filter(entry => entry.id !== id);
        console.log('Updated entries after delete:', updatedEntries);
        setEntries(updatedEntries);
        
        // Refresh the entries list to ensure consistency
        setTimeout(() => {
          console.log('Refreshing entries list...');
          fetchEntries();
        }, 100);
        
        console.log('Entry deleted successfully');
        console.log('=== FRONTEND DELETE END ===');
      } catch (error: any) {
        console.error('=== FRONTEND DELETE ERROR ===');
        console.error('Error deleting entry:', error);
        console.error('Error details:', {
          message: error.message,
          status: error.response?.status,
          data: error.response?.data,
          config: error.config,
          url: error.config?.url,
          method: error.config?.method,
          headers: error.config?.headers
        });
        // Show error message to user
        alert(`Failed to delete entry: ${error.response?.data || error.message}`);
      }
    }
  };

  const handleEdit = (entry: JournalEntry) => {
    setEditingEntry(entry);
    setShowModal(true);
  };

  const handleSaveEntry = async (entry: JournalEntry) => {
    try {
      if (editingEntry) {
        await journalAPI.updateEntry(editingEntry.id!, entry);
        setEntries(entries.map(e => e.id === editingEntry.id ? { ...entry, id: e.id } : e));
      } else {
        const response = await journalAPI.createEntry(entry);
        setEntries([...entries, response.data]);
      }
      setShowModal(false);
      setEditingEntry(null);
    } catch (error) {
      console.error('Error saving entry:', error);
    }
  };

  const filteredEntries = entries.filter(entry => {
    const matchesSearch = entry.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         entry.content.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesFilter = filterSentiment === 'all' || entry.sentiment === filterSentiment;
    return matchesSearch && matchesFilter;
  });

  const getSentimentIcon = (sentiment?: string) => {
    switch (sentiment) {
      case 'POSITIVE':
        return <Smile className="w-5 h-5 text-green-500" />;
      case 'NEGATIVE':
        return <Frown className="w-5 h-5 text-red-500" />;
      case 'NEUTRAL':
        return <Meh className="w-5 h-5 text-yellow-500" />;
      default:
        return null;
    }
  };

  const getSentimentColor = (sentiment?: string) => {
    switch (sentiment) {
      case 'POSITIVE':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'NEGATIVE':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'NEUTRAL':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStats = () => {
    const total = entries.length;
    const positive = entries.filter(e => e.sentiment === 'POSITIVE').length;
    const negative = entries.filter(e => e.sentiment === 'NEGATIVE').length;
    const neutral = entries.filter(e => e.sentiment === 'NEUTRAL').length;
    
    return { total, positive, negative, neutral };
  };

  const stats = getStats();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 via-white to-secondary-50">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="w-12 h-12 border-4 border-primary-500 border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 via-white to-secondary-50">
      {/* Enhanced Header */}
      <motion.header
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white/90 backdrop-blur-sm border-b border-white/20 shadow-sm"
      >
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="flex items-center space-x-4"
            >
              <motion.div
                animate={{ rotate: [0, 10, -10, 0] }}
                transition={{ duration: 2, repeat: Infinity, repeatDelay: 3 }}
                className="w-12 h-12 bg-gradient-to-r from-primary-500 to-secondary-500 rounded-xl flex items-center justify-center shadow-lg"
              >
                <BookOpen className="w-7 h-7 text-white" />
              </motion.div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Journal App</h1>
                <p className="text-sm text-gray-600">{greeting}</p>
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              className="flex items-center space-x-4"
            >
              <div className="flex items-center space-x-2 text-gray-600 bg-gray-100 px-3 py-2 rounded-lg">
                <User className="w-5 h-5" />
                <span className="font-medium">{user?.userName}</span>
              </div>
              <button className="p-2 text-gray-400 hover:text-gray-600 transition-colors rounded-lg hover:bg-gray-100">
                <Bell className="w-5 h-5" />
              </button>
              <button className="p-2 text-gray-400 hover:text-gray-600 transition-colors rounded-lg hover:bg-gray-100">
                <Settings className="w-5 h-5" />
              </button>
              <button
                onClick={logout}
                className="flex items-center space-x-2 text-gray-600 hover:text-red-600 transition-colors px-3 py-2 rounded-lg hover:bg-red-50"
              >
                <LogOut className="w-5 h-5" />
                <span>Logout</span>
              </button>
            </motion.div>
          </div>
        </div>
      </motion.header>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Cards */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8"
        >
          <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Entries</p>
                <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
              </div>
              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                <BookOpen className="w-6 h-6 text-primary-600" />
              </div>
            </div>
          </div>
          
          <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Positive</p>
                <p className="text-2xl font-bold text-green-600">{stats.positive}</p>
              </div>
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                <Smile className="w-6 h-6 text-green-600" />
              </div>
            </div>
          </div>
          
          <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Neutral</p>
                <p className="text-2xl font-bold text-yellow-600">{stats.neutral}</p>
              </div>
              <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                <Meh className="w-6 h-6 text-yellow-600" />
              </div>
            </div>
          </div>
          
          <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Negative</p>
                <p className="text-2xl font-bold text-red-600">{stats.negative}</p>
              </div>
              <div className="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                <Frown className="w-6 h-6 text-red-600" />
              </div>
            </div>
          </div>
        </motion.div>

        {/* Enhanced Controls */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl p-6 shadow-lg border border-gray-200 mb-8"
        >
          <div className="flex flex-col lg:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Search entries by title or content..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200"
              />
            </div>
            <div className="flex items-center space-x-4">
              <div className="relative">
                <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <select
                  value={filterSentiment}
                  onChange={(e) => setFilterSentiment(e.target.value)}
                  className="pl-10 pr-8 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200 appearance-none bg-white"
                >
                  <option value="all">All Sentiments</option>
                  <option value="POSITIVE">Positive</option>
                  <option value="NEGATIVE">Negative</option>
                  <option value="NEUTRAL">Neutral</option>
                </select>
              </div>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => setShowModal(true)}
                className="px-6 py-3 bg-gradient-to-r from-primary-600 to-primary-700 hover:from-primary-700 hover:to-primary-800 text-white font-semibold rounded-lg transition-all duration-200 flex items-center space-x-2 shadow-lg hover:shadow-xl"
              >
                <Plus className="w-5 h-5" />
                <span>New Entry</span>
              </motion.button>
            </div>
          </div>
        </motion.div>

        {/* Enhanced Entries Grid */}
        <AnimatePresence>
          {filteredEntries.length === 0 ? (
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-white rounded-xl p-12 text-center shadow-lg border border-gray-200"
            >
              <BookOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-gray-900 mb-2">No entries found</h3>
              <p className="text-gray-600 mb-6">
                {searchTerm || filterSentiment !== 'all' 
                  ? 'Try adjusting your search or filter criteria'
                  : 'Start writing your first journal entry to begin your journey!'
                }
              </p>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => setShowModal(true)}
                className="px-6 py-3 bg-gradient-to-r from-primary-600 to-primary-700 hover:from-primary-700 hover:to-primary-800 text-white font-semibold rounded-lg transition-all duration-200 flex items-center space-x-2 mx-auto shadow-lg hover:shadow-xl"
              >
                <Plus className="w-5 h-5" />
                <span>Create Your First Entry</span>
              </motion.button>
            </motion.div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredEntries.map((entry, index) => (
                <motion.div
                  key={entry.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 border border-gray-200 overflow-hidden"
                >
                  <div className="p-6">
                    <div className="flex justify-between items-start mb-4">
                      <div className="flex-1">
                        <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
                          {entry.title}
                        </h3>
                        <div className="flex items-center space-x-2 mb-3">
                          <Calendar className="w-4 h-4 text-gray-400" />
                          <span className="text-sm text-gray-500">
                            {new Date(entry.date || '').toLocaleDateString()}
                          </span>
                          {entry.sentiment && getSentimentIcon(entry.sentiment)}
                        </div>
                      </div>
                      <div className="flex space-x-1">
                        <button
                          onClick={() => handleEdit(entry)}
                          className="p-2 text-gray-400 hover:text-primary-600 transition-colors rounded-lg hover:bg-primary-50"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(entry.id!)}
                          className="p-2 text-gray-400 hover:text-red-600 transition-colors rounded-lg hover:bg-red-50"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                    
                    <p className="text-gray-600 line-clamp-3 mb-4">
                      {entry.content}
                    </p>
                    
                    {entry.sentiment && (
                      <div className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium border ${getSentimentColor(entry.sentiment)}`}>
                        {entry.sentiment.toLowerCase()}
                      </div>
                    )}
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </AnimatePresence>
      </div>

      {/* Journal Entry Modal */}
      {showModal && (
        <JournalEntryModal
          entry={editingEntry}
          onSave={handleSaveEntry}
          onClose={() => {
            setShowModal(false);
            setEditingEntry(null);
          }}
        />
      )}
    </div>
  );
};

export default Dashboard;
