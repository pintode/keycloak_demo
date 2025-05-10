'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function UserInfo() {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const router = useRouter();

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          router.push('/login');
          return;
        }

        const response = await fetch('http://localhost:8081/api/user/info', {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          if (response.status === 401) {
            localStorage.removeItem('accessToken');
            router.push('/login');
            return;
          }
          throw new Error('Failed to fetch user data');
        }

        const data = await response.json();
        setUserData(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to fetch data');
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
  }, [router]);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    router.push('/login');
  };

  if (isLoading) return <div className="text-center py-10">Loading...</div>;
  if (error) return <div className="text-red-500 text-center py-10">{error}</div>;
  if (!userData) return null;

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
      <h1 className="text-2xl font-bold text-center mb-6">User Information</h1>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-500">Email</label>
          <p className="mt-1 text-sm text-gray-900">{userData.email}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-500">First Name</label>
          <p className="mt-1 text-sm text-gray-900">{userData.firstName}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-500">Last Name</label>
          <p className="mt-1 text-sm text-gray-900">{userData.lastName}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-500">Roles</label>
          <div className="mt-1">
            {userData.roles.map((role: string, index: number) => (
              <span key={index} className="inline-block bg-gray-100 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2">
                {role}
              </span>
            ))}
          </div>
        </div>
      </div>

      <button
        onClick={handleLogout}
        className="mt-6 w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
      >
        Logout
      </button>
    </div>
  );
}