import { useState } from "react";
import { cn } from "@/lib/utils";

interface AvatarProps {
  src?: string | null;
  firstName?: string;
  lastName?: string;
  /** Classes Tailwind pour la taille, ex: "h-8 w-8 text-xs" */
  className?: string;
}

/**
 * Avatar avec fallback automatique.
 * Si l'image échoue à charger (404, réseau, fichier supprimé),
 * affiche les initiales de l'utilisateur à la place.
 */
export function Avatar({
  src,
  firstName = "",
  lastName = "",
  className,
}: Readonly<AvatarProps>) {
  const [hasError, setHasError] = useState(false);

  const initials =
    `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase() || "?";

  const showImage = !!src && !hasError;

  if (showImage) {
    return (
      <img
        src={src}
        alt={`${firstName} ${lastName}`}
        className={cn("rounded-full object-cover", className)}
        onError={() => setHasError(true)}
      />
    );
  }

  return (
    <div
      role="img"
      aria-label={`${firstName} ${lastName}`}
      className={cn(
        "flex items-center justify-center rounded-full bg-primary text-primary-foreground font-bold select-none",
        className,
      )}
    >
      {initials}
    </div>
  );
}
