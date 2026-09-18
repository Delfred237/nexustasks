import { apiClient } from "@/lib/api/client";
import { ENDPOINTS } from "@/lib/api/endpoints";
import type { User } from "@/features/auth/types/auth.types";

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ChangePasswordResponse {
  message: string;
  otherSessionsRevoked: boolean;
}

export const profileService = {
  async updateProfile(data: UpdateProfileRequest): Promise<User> {
    const response = await apiClient.patch<User>(
      ENDPOINTS.users.updateProfile,
      data,
    );
    return response.data;
  },

  async changePassword(
    data: ChangePasswordRequest,
  ): Promise<ChangePasswordResponse> {
    const response = await apiClient.patch<ChangePasswordResponse>(
      ENDPOINTS.users.changePassword,
      data,
    );
    return response.data;
  },

  async uploadAvatar(file: File): Promise<{ avatarUrl: string }> {
    const formData = new FormData();
    formData.append("file", file);

    const response = await apiClient.post<{ avatarUrl: string }>(
      ENDPOINTS.users.uploadAvatar,
      formData,
      { headers: { "Content-Type": "multipart/form-data" } },
    );
    return response.data;
  },

  async deleteAvatar(): Promise<void> {
    await apiClient.delete(ENDPOINTS.users.deleteAvatar);
  },
};
