export interface JournalEntry {
  id?: string;
  title: string;
  content: string;
  date?: string;
  sentiment?: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
}

export interface User {
  id?: string;
  userName: string;
  email: string;
  password?: string;
  sentimentAnalysis?: boolean;
}

export interface UserDTO {
  userName: string;
  email: string;
  password: string;
  sentimentAnalysis: boolean;
}

export interface LoginRequest {
  userName: string;
  password: string;
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

export interface WeatherResponse {
  current: {
    tempC: number;
    condition: {
      text: string;
      icon: string;
    };
  };
}
