import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Save, Calendar, FileText, Clock, AlertCircle } from 'lucide-react';
import { JournalEntry } from '../types';

interface JournalEntryModalProps {
  entry?: JournalEntry | null;
  onSave: (entry: JournalEntry) => void;
  onClose: () => void;
}

const JournalEntryModal: React.FC<JournalEntryModalProps> = ({
  entry,
  onSave,
  onClose,
}) => {
  const [formData, setFormData] = useState<JournalEntry>({
    title: '',
    content: '',
    date: new Date().toISOString().split('T')[0],
    sentiment: 'NEUTRAL',
  });

  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  useEffect(() => {
    if (entry) {
      setFormData({
        title: entry.title,
        content: entry.content,
        date: entry.date ? new Date(entry.date).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
        sentiment: entry.sentiment || 'NEUTRAL',
      });
    }
  }, [entry]);

  const validateForm = () => {
    const newErrors: { [key: string]: string } = {};
    
    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }
    
    if (!formData.content.trim()) {
      newErrors.content = 'Content is required';
    }
    
    if (!formData.date) {
      newErrors.date = 'Date is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setLoading(true);

    try {
      const entryData: JournalEntry = {
        ...formData,
        date: new Date(formData.date || new Date()).toISOString(),
      };
      
      await onSave(entryData);
    } catch (error) {
      console.error('Error saving entry:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const getCharacterCount = () => {
    return formData.content?.length || 0;
  };

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center p-4"
        onClick={onClose}
      >
        <motion.div
          initial={{ opacity: 0, scale: 0.9, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.9, y: 20 }}
          transition={{ type: "spring", damping: 25, stiffness: 300 }}
          className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] border border-gray-200 flex flex-col"
          onClick={(e) => e.stopPropagation()}
        >
          {/* Enhanced Header */}
          <div className="flex items-center justify-between p-6 border-b border-gray-200 bg-gradient-to-r from-primary-50 to-secondary-50 flex-shrink-0">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-primary-500 to-secondary-500 rounded-lg flex items-center justify-center">
                <FileText className="w-6 h-6 text-white" />
              </div>
              <div>
                <h2 className="text-2xl font-bold text-gray-900">
                  {entry ? 'Edit Journal Entry' : 'New Journal Entry'}
                </h2>
                <p className="text-sm text-gray-600">
                  {entry ? 'Update your journal entry' : 'Create a new journal entry'}
                </p>
              </div>
            </div>
            <motion.button
              whileHover={{ scale: 1.1, rotate: 90 }}
              whileTap={{ scale: 0.9 }}
              onClick={onClose}
              className="p-2 text-gray-400 hover:text-gray-600 transition-colors rounded-full hover:bg-gray-100"
            >
              <X className="w-6 h-6" />
            </motion.button>
          </div>

          {/* Enhanced Form */}
          <form onSubmit={handleSubmit} className="flex flex-col flex-1 overflow-hidden">
            <div className="flex-1 overflow-y-auto p-6 space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Title Section */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="lg:col-span-2"
              >
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Entry Title *
                </label>
                <div className="relative">
                  <input
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200 ${
                      errors.title ? 'border-red-300 bg-red-50' : 'border-gray-300'
                    }`}
                    placeholder="Enter a meaningful title for your entry..."
                    required
                  />
                  {errors.title && (
                    <div className="flex items-center mt-2 text-red-600 text-sm">
                      <AlertCircle className="w-4 h-4 mr-1" />
                      {errors.title}
                    </div>
                  )}
                </div>
              </motion.div>

              {/* Date Section */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2 }}
              >
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Entry Date *
                </label>
                <div className="relative">
                  <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <input
                    type="date"
                    name="date"
                    value={formData.date}
                    onChange={handleInputChange}
                    className={`w-full px-4 py-3 pl-10 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200 ${
                      errors.date ? 'border-red-300 bg-red-50' : 'border-gray-300'
                    }`}
                    required
                  />
                  {errors.date && (
                    <div className="flex items-center mt-2 text-red-600 text-sm">
                      <AlertCircle className="w-4 h-4 mr-1" />
                      {errors.date}
                    </div>
                  )}
                </div>
              </motion.div>

              {/* Sentiment Section */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.25 }}
              >
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  How are you feeling?
                </label>
                <div className="grid grid-cols-3 gap-3">
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, sentiment: 'POSITIVE' }))}
                    className={`p-3 rounded-lg border-2 transition-all duration-200 flex flex-col items-center space-y-1 ${
                      formData.sentiment === 'POSITIVE'
                        ? 'border-green-500 bg-green-50 text-green-700'
                        : 'border-gray-300 hover:border-green-300 hover:bg-green-25'
                    }`}
                  >
                    <span className="text-2xl">😊</span>
                    <span className="text-sm font-medium">Positive</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, sentiment: 'NEUTRAL' }))}
                    className={`p-3 rounded-lg border-2 transition-all duration-200 flex flex-col items-center space-y-1 ${
                      formData.sentiment === 'NEUTRAL'
                        ? 'border-yellow-500 bg-yellow-50 text-yellow-700'
                        : 'border-gray-300 hover:border-yellow-300 hover:bg-yellow-25'
                    }`}
                  >
                    <span className="text-2xl">😐</span>
                    <span className="text-sm font-medium">Neutral</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, sentiment: 'NEGATIVE' }))}
                    className={`p-3 rounded-lg border-2 transition-all duration-200 flex flex-col items-center space-y-1 ${
                      formData.sentiment === 'NEGATIVE'
                        ? 'border-red-500 bg-red-50 text-red-700'
                        : 'border-gray-300 hover:border-red-300 hover:bg-red-25'
                    }`}
                  >
                    <span className="text-2xl">😔</span>
                    <span className="text-sm font-medium">Negative</span>
                  </button>
                </div>
              </motion.div>

              {/* Character Count */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.35 }}
                className="flex items-center justify-end"
              >
                <div className="flex items-center space-x-2 text-sm text-gray-500">
                  <Clock className="w-4 h-4" />
                  <span>{getCharacterCount()} characters</span>
                </div>
              </motion.div>
            </div>

            {/* Content Section */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.45 }}
            >
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Journal Content *
              </label>
              <div className="relative">
                <textarea
                  name="content"
                  value={formData.content}
                  onChange={handleInputChange}
                  className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200 min-h-[200px] resize-none ${
                    errors.content ? 'border-red-300 bg-red-50' : 'border-gray-300'
                  }`}
                  placeholder="Write your thoughts, feelings, and experiences here. Be as detailed as you'd like..."
                  required
                />
                {errors.content && (
                  <div className="flex items-center mt-2 text-red-600 text-sm">
                    <AlertCircle className="w-4 h-4 mr-1" />
                    {errors.content}
                  </div>
                )}
              </div>
            </motion.div>
            </div>

            {/* Enhanced Action Buttons - Fixed Footer */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              className="flex justify-end space-x-4 p-6 border-t border-gray-200 bg-gray-50 flex-shrink-0"
            >
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="button"
                onClick={onClose}
                className="px-6 py-3 text-gray-600 hover:text-gray-800 font-medium transition-colors rounded-lg hover:bg-gray-100"
              >
                Cancel
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                disabled={loading || !formData.title.trim() || !formData.content.trim()}
                className="px-6 py-3 bg-gradient-to-r from-primary-600 to-primary-700 hover:from-primary-700 hover:to-primary-800 text-white font-semibold rounded-lg transition-all duration-200 flex items-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-xl"
              >
                {loading ? (
                  <>
                    <motion.div
                      animate={{ rotate: 360 }}
                      transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                      className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                    />
                    <span>Saving...</span>
                  </>
                ) : (
                  <>
                    <Save className="w-5 h-5" />
                    <span>{entry ? 'Update Entry' : 'Save Entry'}</span>
                  </>
                )}
              </motion.button>
            </motion.div>
          </form>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
};

export default JournalEntryModal;
