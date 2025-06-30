# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    prompt_string: .asciiz "Ingrese texto: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Constantes booleanas del lenguaje
    true_const:   .word 1
    false_const:  .word 0
    luna_const:   .word 1    # luna = true
    sol_const:    .word 0    # sol = false

    # Variables del programa
    a_var: .word 0
    b_var: .word 0
    arbol_var: .word 0
    planta_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ab_intermediate.txt
main:
    # Prólogo estándar main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función genérica - guardar hasta 4 parámetros en stack frame
    sw $a0, -4($fp)   # param1 local
    sw $a1, -8($fp)   # param2 local
    sw $a2, -12($fp)  # param3 local
    sw $a3, -16($fp)  # param4 local

    # Inicio de función
    # DECLARE a INT
    # a = 10
    li $t0, 10
    sw $t0, a_var

    # DECLARE b INT
    # b = 5
    li $t0, 5
    sw $t0, b_var

    # t1 = a + b
    lw $t1, a_var
    lw $t2, b_var
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE arbol INT
    # arbol = t1
    lw $t0, t1_var
    sw $t0, arbol_var

    # t2 = a - b
    lw $t1, a_var
    lw $t2, b_var
    sub $t0, $t1, $t2
    sw $t0, t2_var

    # DECLARE planta INT
    # planta = t2
    lw $t0, t2_var
    sw $t0, planta_var

    # WRITE arbol
    lw $a0, arbol_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE planta
    lw $a0, planta_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo estándar main
exit_main:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

print_float_decimal:
    # $a0 contiene el flotante multiplicado por 100
    # Guardar $a0 en el stack para uso posterior
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    
    # Verificar si es negativo
    bgez $a0, positive_float
    
    # Imprimir signo negativo
    li $v0, 11
    li $a0, 45    # ASCII de '-'
    syscall
    
    # Convertir a positivo
    lw $a0, 0($sp)   # Recargar valor original
    sub $a0, $zero, $a0
    
positive_float:
    # Dividir por 100 para separar parte entera y decimal
    li $t1, 100
    div $a0, $t1
    mflo $t2        # Parte entera
    mfhi $t3        # Resto (parte decimal * 100)
    
    # Imprimir parte entera
    move $a0, $t2
    li $v0, 1
    syscall
    
    # Imprimir punto decimal
    li $v0, 11
    li $a0, 46    # ASCII de '.'
    syscall
    
    # Imprimir parte decimal
    # Si es menor que 10, agregar un 0 adelante
    bge $t3, 10, print_decimal
    li $v0, 11
    li $a0, 48    # ASCII de '0'
    syscall
    
print_decimal:
    move $a0, $t3
    li $v0, 1
    syscall
    
    # Restaurar stack
    addi $sp, $sp, 4
    jr $ra

power_function:
    li $v0, 1
    beq $a1, $zero, power_done
    blt $a1, $zero, power_negative
power_loop:
    mult $v0, $a0
    mflo $v0
    addi $a1, $a1, -1
    bne $a1, $zero, power_loop
power_done:
    jr $ra
power_negative:
    li $v0, 0
    jr $ra

