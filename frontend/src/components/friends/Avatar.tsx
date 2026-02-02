import type { ReactNode } from "react";

interface AvatarProps {
  initial: string;
  color: string;
  size?: "sm" | "md" | "lg";
  className?: string;
  children?: ReactNode;
}

const sizeClasses = {
  sm: "w-8 h-8 text-xs",
  md: "w-10 h-10 text-sm",
  lg: "w-12 h-12 text-base",
};

const sizePx = { sm: 32, md: 40, lg: 48 };

export function Avatar({ initial, color, size = "md", className = "", children }: AvatarProps) {
  const px = sizePx[size];
  return (
    <div
      className={`rounded-full flex items-center justify-center font-semibold text-white shrink-0 ${sizeClasses[size]} ${className}`}
      style={{
        backgroundColor: color,
        width: px,
        height: px,
        minWidth: px,
        maxWidth: px,
        minHeight: px,
        maxHeight: px,
      }}
      aria-hidden
    >
      {children ?? initial.toUpperCase()}
    </div>
  );
}
