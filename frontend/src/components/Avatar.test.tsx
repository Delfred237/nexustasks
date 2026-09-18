import { describe, it, expect } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { Avatar } from './Avatar'

describe('Avatar', () => {
  it("affiche l'image quand src est fourni", () => {
    render(<Avatar src="/api/files/avatars/a.png" firstName="Alice" lastName="Martin" />)
    expect(screen.getByAltText('Alice Martin')).toBeInTheDocument()
  })

  it('affiche les initiales quand src est absent', () => {
    render(<Avatar src={null} firstName="Alice" lastName="Martin" />)
    expect(screen.queryByAltText('Alice Martin')).not.toBeInTheDocument()
    expect(screen.getByText('AM')).toBeInTheDocument()
  })

  it('bascule sur les initiales quand le chargement de l\'image échoue', () => {
    // Ce test vérifie le fix du bug "avatar cassé"
    render(<Avatar src="/broken.png" firstName="Bob" lastName="Smith" />)

    const img = screen.getByAltText('Bob Smith')
    fireEvent.error(img) // Simule une image 404

    expect(screen.queryByAltText('Bob Smith')).not.toBeInTheDocument()
    expect(screen.getByText('BS')).toBeInTheDocument()
  })

  it('affiche un point d\'interrogation sans nom', () => {
    render(<Avatar src={null} />)
    expect(screen.getByText('?')).toBeInTheDocument()
  })
})