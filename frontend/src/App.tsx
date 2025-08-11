import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './components/Login';
import Dashboard from './components/Dashboard';

// Simple test component to debug
const TestComponent: React.FC = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 to-secondary-50">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">Journal App</h1>
        <p className="text-gray-600">Frontend is working! 🎉</p>
        <div className="mt-4 space-x-4">
          <a href="/login" className="btn-primary">Go to Login</a>
          <a href="/test" className="btn-secondary">Test Page</a>
        </div>
      </div>
    </div>
  );
};

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  
  return isAuthenticated ? (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      {children}
    </motion.div>
  ) : (
    <Navigate to="/login" replace />
  );
};

const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <Router>
      <AnimatePresence mode="wait">
        <Routes>
          <Route path="/test" element={<TestComponent />} />
          <Route
            path="/login"
            element={
              isAuthenticated ? (
                <Navigate to="/dashboard" replace />
              ) : (
                <Login />
              )
            }
          />
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/"
            element={<TestComponent />}
          />
        </Routes>
      </AnimatePresence>
    </Router>
  );
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

export default App;
