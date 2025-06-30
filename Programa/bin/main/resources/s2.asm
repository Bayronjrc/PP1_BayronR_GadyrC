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
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    contador_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s2_intermediate.txt
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
    # DECLARE contador INT
    # contador = 1
    li $t0, 1
    sw $t0, contador_var

L1:
    # WRITE contador
    lw $a0, contador_var
    jal print_int
    la $a0, nl
    jal print_string

    # t1 = contador + 1
    lw $t1, contador_var
    li $t2, 1
    add $t0, $t1, $t2
    sw $t0, t1_var

    # contador = t1
    lw $t0, t1_var
    sw $t0, contador_var

    # t2 = contador <= 6
    lw $t1, contador_var
    li $t2, 6
    sle $t0, $t1, $t2
    sw $t0, t2_var

    # IF t2 GOTO L1
    lw $t0, t2_var
    bne $t0, $zero, L1

L2:

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

