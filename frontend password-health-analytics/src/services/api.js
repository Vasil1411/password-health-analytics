// Базов URL към твоя Spring Boot бекенд
const API_BASE_URL = 'http://localhost:8080/api/v1';

export const analyzePassword = async (password) => {
  try {
    const response = await fetch(`${API_BASE_URL}/password/analyze`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      // Изпращаме точно ключа 'password' в JSON обекта
      body: JSON.stringify({ password: password }),
    });

    if (!response.ok) {
      // Това ще ни изпише подробна грешка в конзолата, ако пак върне 400
      const errorText = await response.text();
      console.error('Детайли за грешката от сървъра:', errorText);
      throw new Error(`Грешка от сървъра: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Грешка при analyzePassword:', error);
    throw error;
  }
};

/**
 * 2. Заявка за статистиката в Дашборда
 
 */
export const fetchDashboardSummary = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/analytics/summary`);

    if (!response.ok) {
      throw new Error(`Грешка от сървъра: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Грешка при fetchDashboardSummary:', error);
    throw error;
  }
};