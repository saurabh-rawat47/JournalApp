# Journal App Backend 

A modern, animated React frontend for the Journal Application with beautiful UI and smooth animations.

## Features

- 🎨 **Beautiful Animations**: Smooth transitions and micro-interactions using Framer Motion
- 🔐 **Authentication**: Secure login/signup with JWT token management
- 📝 **Journal Management**: Create, read, update, and delete journal entries
- 🔍 **Search & Filter**: Search entries by title/content and filter by sentiment
- 📱 **Responsive Design**: Works perfectly on desktop, tablet, and mobile
- 🎯 **Modern UI**: Clean, intuitive interface with Tailwind CSS
- ⚡ **Real-time Updates**: Instant feedback and state management

## Tech Stack

- **React 18** with TypeScript
- **Framer Motion** for animations
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API communication
- **Lucide React** for icons

## Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- Backend server running on `http://localhost:8080`

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. Open [http://localhost:3000](http://localhost:3000) in your browser.

### Building for Production

```bash
npm run build
```

## Project Structure

```
frontend/
├── src/
│   ├── components/          # React components
│   │   ├── Login.tsx       # Authentication component
│   │   ├── Dashboard.tsx   # Main dashboard
│   │   └── JournalEntryModal.tsx # Entry creation/editing modal
│   ├── contexts/           # React contexts
│   │   └── AuthContext.tsx # Authentication state management
│   ├── services/           # API services
│   │   └── api.ts         # API communication layer
│   ├── types/             # TypeScript type definitions
│   │   └── index.ts       # Application types
│   ├── App.tsx           # Main app component with routing
│   └── index.css         # Global styles and Tailwind imports
├── public/               # Static assets
├── package.json          # Dependencies and scripts
├── tailwind.config.js   # Tailwind CSS configuration
└── tsconfig.json        # TypeScript configuration
```

## Features in Detail

### Authentication
- Secure login/signup with JWT tokens
- Automatic token management and refresh
- Protected routes with authentication guards
- Persistent login state

### Journal Entries
- Create new journal entries with title, content, and date
- Edit existing entries with a beautiful modal interface
- Delete entries with confirmation
- View all entries in a responsive grid layout

### Search & Filter
- Real-time search through entry titles and content
- Filter entries by sentiment (Positive, Negative, Neutral)
- Combined search and filter functionality

### Animations
- Page transitions with Framer Motion
- Micro-interactions on buttons and cards
- Loading states with animated spinners
- Smooth modal animations
- Hover effects and scale animations

### Responsive Design
- Mobile-first approach
- Responsive grid layout
- Touch-friendly interface
- Optimized for all screen sizes

## API Integration

The frontend communicates with the backend through the following endpoints:

- **Authentication**: `/public/login`, `/public/signup`
- **User Profile**: `/user`
- **Journal Entries**: `/journal`

All API calls are handled through the `api.ts` service with automatic token management and error handling.

## Customization

### Colors
The app uses a custom color palette defined in `tailwind.config.js`:
- Primary colors (blue theme)
- Secondary colors (purple theme)
- Custom animations and keyframes

### Animations
Custom animations are defined in the Tailwind config and can be easily modified:
- Fade-in effects
- Slide animations
- Scale transitions
- Bounce effects

## Development

### Adding New Components
1. Create your component in the `src/components/` directory
2. Import and use Framer Motion for animations
3. Use Tailwind CSS classes for styling
4. Follow the existing TypeScript patterns

### Styling Guidelines
- Use Tailwind CSS utility classes
- Follow the established color palette
- Implement responsive design
- Add smooth animations for better UX

### State Management
- Use React Context for global state (authentication)
- Use local state for component-specific data
- Implement proper error handling
- Follow React best practices

## Troubleshooting

### Common Issues

1. **Backend Connection Error**
   - Ensure the backend server is running on port 8080
   - Check CORS configuration in the backend
   - Verify API endpoints are correct

2. **Authentication Issues**
   - Clear browser localStorage
   - Check JWT token format
   - Verify authentication endpoints

3. **Build Errors**
   - Clear node_modules and reinstall dependencies
   - Check TypeScript configuration
   - Verify all imports are correct

## Contributing

1. Follow the existing code structure
2. Add proper TypeScript types
3. Include animations for new features
4. Test on multiple screen sizes
5. Follow the established naming conventions

## License

This project is part of the Journal Application and follows the same license as the main project.
